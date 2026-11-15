package app.autopayguard.spike

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/** Minimal screen: explains the permission and opens the system notification-access settings. */
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(48, 96, 48, 48) }
        layout.addView(TextView(this).apply {
            text = "AutoPay Guard spike\n\nGrant notification access so the app can spot AutoPay and debit alerts. " +
                "Messages are parsed on this phone only; nothing is sent anywhere.\n\n" +
                "You can also share an SMS text to this app from your messages app."
            textSize = 16f
        })
        layout.addView(Button(this).apply {
            text = "Open notification access settings"
            setOnClickListener { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
        })
        setContentView(layout)
    }
}
