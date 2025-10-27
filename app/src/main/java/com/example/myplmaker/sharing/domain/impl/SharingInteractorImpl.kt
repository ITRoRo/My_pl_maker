package com.example.myplmaker.sharing.domain.impl

import android.content.Context
import com.example.myplmaker.R
import com.example.myplmaker.sharing.domain.SharingRepository
import com.example.myplmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(private val repository: SharingRepository,  private val app: Context) : SharingInteractor {

    override fun buttonShare() {
        repository.buttonShare(getLinkApp())
    }

    override fun buttonSupport() {
        repository.buttonSupport(getEmail(), getSubject(), getMessage())
    }

    override fun buttonConsent() {
        repository.buttonConsent(getLinkPolicy())
    }

    override fun getLinkApp(): String {
        return app.getString(R.string.share_message)
    }

    override fun getEmail(): String {
        return app.getString(R.string.account)
    }

    override fun getSubject(): String {
        return app.getString(R.string.subject)
    }

    override fun getMessage(): String {
        return app.getString(R.string.thanks_message)
    }

    override fun getLinkPolicy(): String {
        return app.getString(R.string.email)
    }
}