package com.example.myplmaker.player.domain.impl

import com.example.myplmaker.player.domain.PlayerInteractor
import com.example.myplmaker.player.domain.PlayerRepository
import com.example.myplmaker.search.domain.model.Track

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {

    override fun prepareTrack(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        track.previewUrl?.let { url ->
            playerRepository.prepareMedia(url, onPrepared, onCompletion)
        }
    }

    override fun playTrack() {
        playerRepository.startPlayer()
    }

    override fun pauseTrack() {
        playerRepository.pausePlayer()
    }

    override fun releaseResources() {
        playerRepository.releasePlayer()
    }

    override fun getCurrentPosition(): Long {
        return playerRepository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }
}