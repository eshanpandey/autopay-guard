# Data-access spike (Android)

Purpose: find out whether Google Play accepts a notification-listener based tracker,
and prove the share-to-app fallback. NOT compiled in CI yet: the cloud sandbox has no
Android SDK, so open this folder in Android Studio and add the `:parser` module as a dependency.

Files (copy into a new Empty Activity project, package `app.autopayguard.spike`):
- `TxnNotificationListener.kt` - NotificationListenerService that feeds notification text to `MessageParser`.
- `ShareReceiverActivity.kt` - receives text shared from the SMS/bank app (no permission needed).
- `AndroidManifest.snippet.xml` - the service and activity declarations.

Everything parses on-device; nothing is sent over the network (NFR-1).
