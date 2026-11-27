package com.melinda.sketchmate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Sketchmate AR as a real Android app.
 *
 * The whole app is one self-contained page in
 * app/src/main/assets/sketchmate-ar.html, shown in a WebView:
 *  - AR Trace uses the PHONE camera only (never the Wyze cam).
 *  - 14 built-in black-and-white line drawings.
 *  - Photo import turns a photo into black-and-white line art.
 *  - Draw Together is friends-only, by a 4-letter room code.
 *    No random strangers, no copyrighted characters.
 *  - Pivot Mate is a bot built into the page that draws with you.
 *
 * Everything except the optional friend rooms works offline.
 * The PeerJS library is bundled locally in assets/peerjs.min.js,
 * so the page does not need the internet just to open.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    // The page's <input type="file" accept="image/*"> for photo import.
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    private val pickPhoto = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val callback = fileChooserCallback
        fileChooserCallback = null
        if (uri != null) {
            callback?.onReceiveValue(arrayOf(uri))
        } else {
            callback?.onReceiveValue(null)
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // Nothing to do here: the user just taps the AR button again
        // and the camera request goes through with permission granted.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for the camera up front so AR Trace works on the first tap.
        // If Melinda says no, the app still opens and draws — AR just
        // cannot start until the camera is allowed.
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        webView = WebView(this)
        setContentView(webView)

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        // The page draws on <canvas> and uses touch; keep zoom off
        // so the paper does not jump around while drawing.
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()
                // Keep the local app page inside the WebView.
                if (url.startsWith("file:///android_asset/")) return false
                // Anything else (a link out of the app) opens in the
                // phone's browser instead.
                return try {
                    startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    )
                    true
                } catch (_: Exception) {
                    false
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {

            // The page asks for the camera with getUserMedia for AR Trace.
            // Grant video only, and only if Android already gave us the
            // CAMERA permission. Never grant audio: the app has no mic use.
            override fun onPermissionRequest(request: PermissionRequest) {
                val wanted = request.resources.filter {
                    it == PermissionRequest.RESOURCE_VIDEO_CAPTURE
                }.toTypedArray()
                val cameraOk = ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (cameraOk && wanted.isNotEmpty()) {
                    request.grant(wanted)
                } else {
                    request.deny()
                    if (!cameraOk) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            1
                        )
                    }
                }
            }

            // The page's photo picker: <input type="file" accept="image/*">.
            // Open the phone's photo picker and hand the chosen photo back.
            override fun onShowFileChooser(
                view: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = filePathCallback
                pickPhoto.launch("image/*")
                return true
            }
        }

        webView.loadUrl("file:///android_asset/sketchmate-ar.html")
    }

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }
}
