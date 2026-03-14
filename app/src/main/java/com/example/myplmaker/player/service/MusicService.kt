package com.example.myplmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.myplmaker.R
import com.example.myplmaker.player.ui.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicService : Service(), AudioPlayerControl {

    private val binder = MusicServiceBinder()
    private var mediaPlayer: MediaPlayer? = null

    private val _playerState = MutableStateFlow(PlayerState.DEFAULT)
    private val playerState = _playerState.asStateFlow()

    private val _currentPosition = MutableStateFlow("00:00")
    private val currentPosition = _currentPosition.asStateFlow()

    private var timerJob: Job? = null
    private var songUrl = ""
    private var trackTitle = ""
    private var artistName = ""

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
        const val SERVICE_NOTIFICATION_ID = 101
    }


    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        songUrl = intent?.getStringExtra("song_url") ?: ""
        trackTitle = intent?.getStringExtra("track_title") ?: "Unknown"
        artistName = intent?.getStringExtra("artist_name") ?: "Unknown"

        preparePlayer(songUrl)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }


    override fun getPlayerState(): StateFlow<PlayerState> = playerState
    override fun getCurrentPosition(): StateFlow<String> = currentPosition

    override fun preparePlayer(trackUrl: String) {
        if (trackUrl.isEmpty()) return

        mediaPlayer?.setDataSource(trackUrl)
        mediaPlayer?.prepareAsync()

        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerState.PREPARED
        }

        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            _playerState.value = PlayerState.PREPARED
            _currentPosition.value = "00:00"
            hideNotification()
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = PlayerState.PLAYING
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        _playerState.value = PlayerState.PAUSED
        timerJob?.cancel()
    }

    override fun releasePlayer() {
        timerJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        _playerState.value = PlayerState.DEFAULT
        _currentPosition.value = "00:00"
        hideNotification()
    }

    override fun showNotification() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }


    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(300L)
                val pos = mediaPlayer?.currentPosition ?: 0
                val minutes = (pos / 1000) / 60
                val seconds = (pos / 1000) % 60
                _currentPosition.value = String.format("%02d:%02d", minutes, seconds)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$artistName - $trackTitle")
            .setSmallIcon(R.drawable.pr_icon_07)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
}