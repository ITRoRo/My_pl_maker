package com.example.myplmaker.player.data.impl

import android.media.MediaPlayer
import com.example.myplmaker.player.domain.PlayerRepository
import java.io.IOException

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    override fun prepareMedia(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener { onPrepared() }
            mediaPlayer.setOnCompletionListener { onCompletion() }
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}