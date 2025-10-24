package com.example.myplmaker.sharing.domain

interface SharingRepository {
    fun buttonShare(messageShare : String)
    fun buttonSupport(account : String, subject : String, message : String)
    fun buttonConsent(emeilAddress : String)
}