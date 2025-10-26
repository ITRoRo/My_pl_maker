package com.example.myplmaker.player.domain

interface PlayerRepository {
    fun prepareMedia(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Long
    fun isPlaying(): Boolean
}