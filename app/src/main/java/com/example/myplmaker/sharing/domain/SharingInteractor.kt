package com.example.myplmaker.sharing.domain

interface SharingInteractor {
    fun buttonShare()
    fun buttonSupport()
    fun buttonConsent()
    fun getLinkApp() : String
    fun getEmail() : String
    fun getSubject() : String
    fun getMessage() : String
    fun getLinkPolicy() : String
}