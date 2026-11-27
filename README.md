# Sketchmate AR — Android app

A real Android app for drawing, tracing and drawing together.
No ads, no paying features, no random strangers. Free.

## Get the APK

Every push to `main` builds the APK with GitHub Actions and attaches it
to a release. Download the newest release here:

https://github.com/AndroidBeam1/sketchmate-ar/releases

Tap the `app-debug.apk` file, then open it on your phone to install.
Your phone may ask to allow installing from your browser — tap Allow.
Then allow the camera when the app asks, so AR Trace can use it.

## What it does (v1.0)

- **AR Trace:** pick one of 14 built-in black-and-white line drawings,
  or import a photo from your phone. The app turns the photo into
  black-and-white line art, all at once, then shows it over your
  **phone camera** (not the Wyze cam) so you can trace it on paper.
  Opacity slider and a lock so the picture stays still while you draw.
- **Draw Together:** one sheet of paper, shared. Draw by yourself,
  draw along with **Pivot Mate**, or invite a **friend** with a
  4-letter room code — friends only, never random strangers.
  Your Undo and Clear buttons only erase your own lines, so nobody
  can scribble over your part and wreck it.
- **Pivot Mate:** a mate built right into the app. It draws one part
  of a picture in teal, then waits while you draw yours. No internet
  partner, no strangers.
- **My Drawings:** draw your own in the app and save it, then use it
  as a tracing picture later.

Everything except the optional friend rooms works offline. The
PeerJS library is bundled in the app, so the page does not need
the internet just to open. Friend rooms do need internet, because
the two phones have to find each other.

No copyrighted characters are in this app. The built-in drawings
are original line drawings made for this app.

## Build it (on your computer)

You need:
- A computer with Android Studio installed (free, from Google).
- Android Studio Hedgehog or newer.
- An Android phone to test on, or the Android emulator.

Steps:
1. Open Android Studio.
2. Open this folder: `sketchmate-ar-android`
3. Let Android Studio download the Gradle files. It needs internet
   the first time.
4. Connect your Samsung Galaxy S23 FE by USB, turn on Developer
   options > USB debugging on the phone, and tap Allow on the phone.
   Or run it in the emulator.
5. Press Run (the green play button) in Android Studio.
   Android Studio builds the APK and installs it on the phone.
6. Open Sketchmate AR on the phone, allow the camera, and try AR Trace.

To make an APK file to keep:
- In Android Studio: Build > Build App Bundle(s) / APK(s) >
  Build APK(s). The APK is in `app/build/outputs/apk/debug/`.

## Files

- `settings.gradle.kts` — project name, app module.
- `build.gradle.kts` — project plugins.
- `app/build.gradle.kts` — app, Kotlin, AndroidX.
- `app/src/main/AndroidManifest.xml` — app name, internet, camera
  and photo permissions. Camera is optional: the app still opens
  and draws without it.
- `app/src/main/java/com/melinda/sketchmate/MainActivity.kt` — a
  WebView that loads the app page from assets, grants the camera
  to the page for AR Trace, and opens the photo picker for import.
- `app/src/main/assets/sketchmate-ar.html` — the whole app page:
  AR Trace, drawings, photo import, Draw Together, Pivot Mate.
- `app/src/main/assets/peerjs.min.js` — PeerJS 1.5.4, bundled
  locally so friend rooms work without a CDN and the app opens offline.
- `app/src/main/res/values/themes.xml` — app theme.
- `app/src/main/res/drawable/ic_launcher.xml` — app icon (pencil).
- `gradle/wrapper/gradle-wrapper.properties` — Gradle version.
- `.github/workflows/build-apk.yml` — GitHub Actions APK build.
