package app.autopayguard.spike

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import app.autopayguard.parser.MessageParser

class ShareReceiverActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = intent?.takeIf { it.action == Intent.ACTION_SEND }?.getStringExtra(Intent.EXTRA_TEXT)
        val parsed = text?.let { MessageParser.parse(it) }
        Toast.makeText(
            this,
            parsed?.let { "${it.kind}: ${it.merchant ?: "?"} Rs ${it.amountRupees ?: "?"}" } ?: "Not a recurring payment message",
            Toast.LENGTH_LONG,
        ).show()
        finish()
    }
}
