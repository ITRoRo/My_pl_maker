package com.example.myplmaker.player.service

import com.example.myplmaker.player.ui.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getPlayerState(): StateFlow<PlayerState>
    fun getCurrentPosition(): StateFlow<String>
    fun startPlayer()
    fun pausePlayer()
    fun preparePlayer(trackUrl: String)
    fun releasePlayer()
    fun showNotification()
    fun hideNotification()
}