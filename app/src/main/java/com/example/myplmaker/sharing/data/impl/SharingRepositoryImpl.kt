package com.example.myplmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.myplmaker.sharing.domain.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository {
    override fun buttonShare(messageShare: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, messageShare)
        context.startActivity(shareIntent)
    }

    override fun buttonSupport(account: String, subject: String, thanksMessage: String) {
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = Uri.parse("mailto:")
        sendIntent.putExtra(Intent.EXTRA_EMAIL, account)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendIntent.putExtra(Intent.EXTRA_TEXT, thanksMessage)
        context.startActivity(sendIntent)
    }

    override fun buttonConsent(emeilAddress: String) {
        val docIntent = Intent(Intent.ACTION_VIEW, Uri.parse(emeilAddress))
        context.startActivity(docIntent)
    }
}


