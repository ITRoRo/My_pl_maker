package com.example.myplmaker.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.myplmaker.App
import com.example.myplmaker.R

import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val sharedPreferences = getSharedPreferences(App.SETTING_KEY, MODE_PRIVATE)
        themeSwitcher.isChecked = sharedPreferences.getBoolean(App.THEM_KEY, false)

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPreferences.edit()
                .putBoolean(App.THEM_KEY, checked)
                .apply()
        }


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
