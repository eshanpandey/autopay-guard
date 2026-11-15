package app.autopayguard.spike

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import app.autopayguard.parser.MessageParser

class TxnNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val text = listOfNotNull(
            extras.getCharSequence("android.title"),
            extras.getCharSequence("android.bigText") ?: extras.getCharSequence("android.text"),
        ).joinToString(" ")
        val parsed = MessageParser.parse(text) ?: return
        // Spike only: log the structured fields, never the raw text.
        Log.i("AutoPayGuard", "pkg=${sbn.packageName} kind=${parsed.kind} merchant=${parsed.merchant} amt=${parsed.amountRupees}")
    }
}
