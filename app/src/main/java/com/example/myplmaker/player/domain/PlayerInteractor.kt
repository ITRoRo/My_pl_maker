package com.example.myplmaker.player.domain

import com.example.myplmaker.search.domain.model.Track

interface PlayerInteractor {
    fun prepareTrack(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun playTrack()
    fun pauseTrack()
    fun releaseResources()
    fun getCurrentPosition(): Long
    fun isPlaying(): Boolean
}