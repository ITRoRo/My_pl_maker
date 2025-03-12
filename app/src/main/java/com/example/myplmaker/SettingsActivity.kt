package com.example.myplmaker

import android.app.Notification.EXTRA_TEXT
import android.content.Intent
import android.content.Intent.EXTRA_SUBJECT
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener { finish() }

        val buttonShare =
            findViewById<com.google.android.material.textview.MaterialTextView>(R.id.share)
        buttonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            val messageShare = getString(R.string.share_message)
            shareIntent.putExtra(Intent.EXTRA_TEXT, messageShare)
            shareIntent.type = "text/plain"
            startActivity(shareIntent)
        }

        val buttonSupport =
            findViewById<com.google.android.material.textview.MaterialTextView>(R.id.support)
        buttonSupport.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SENDTO)
            val account = arrayOf(getString(R.string.account))
            val subject = getString(R.string.subject)
            val thanksMessage = getString(R.string.thanks_message)
            sendIntent.data = Uri.parse("mailto:")
            sendIntent.putExtra(Intent.EXTRA_EMAIL, account)
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            sendIntent.putExtra(Intent.EXTRA_TEXT, thanksMessage)
            startActivity(sendIntent)
        }


        val buttonConsent =
            findViewById<com.google.android.material.textview.MaterialTextView>(R.id.consent)
        buttonConsent.setOnClickListener {
            val emeilAddress = getString(R.string.email)
            val docIntent = Intent(Intent.ACTION_VIEW, Uri.parse(emeilAddress))
            startActivity(docIntent)
        }
    }

}
