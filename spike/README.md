# Data-access spike (Android)

Purpose: find out whether Google Play accepts a notification-listener based tracker,
and prove the share-to-app fallback.

## Run it
1. Install Android Studio. Choose **Open** and pick this `spike/` folder (not the repo root).
2. Let Gradle sync, then run on a phone (USB debugging on, Android 10+).
3. Tap "Open notification access settings" and allow AutoPay Guard.
4. Watch Logcat for the tag `AutoPayGuard`; you'll see parsed fields (never raw text) for matching notifications.
5. Or share an SMS text to the app from your messages app; a toast shows what was parsed.

Status: written in a sandbox without the Android SDK, so it has NOT been compiled. Expect small
fixes on first sync (SDK version prompts, theme name). Everything parses on-device (NFR-1).
