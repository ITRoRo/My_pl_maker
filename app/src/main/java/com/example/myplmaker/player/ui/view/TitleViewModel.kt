package com.example.myplmaker.player.ui.view

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myplmaker.creator.Creator
import com.example.myplmaker.player.domain.PlayerInteractor
import com.example.myplmaker.player.ui.PlayerState
import com.example.myplmaker.player.ui.TrackUiState
import com.example.myplmaker.search.domain.model.Track
import java.util.Locale

class TitleViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _uiState = MutableLiveData<TrackUiState>()
    val uiState: LiveData<TrackUiState> = _uiState

    private lateinit var currentTrack: Track
    private var currentState = PlayerState.DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeUpdateRunnable: Runnable

    companion object {
        private const val POSITION_UPDATE_DELAY = 1000L

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TitleViewModel(Creator.providePlayerInteractor(context)) as T
                }
            }
    }

    fun initTrack(track: Track) {
        currentTrack = track
        _uiState.value = TrackUiState(track, PlayerState.DEFAULT)

        timeUpdateRunnable = object : Runnable {
            override fun run() {
                if (currentState == PlayerState.PLAYING) {
                    updateCurrentPosition()
                    handler.postDelayed(this, POSITION_UPDATE_DELAY)
                }
            }
        }

        preparePlayer()
    }

    private fun preparePlayer() {
        playerInteractor.prepareTrack(
            currentTrack,
            onPrepared = {
                currentState = PlayerState.PREPARED
                _uiState.postValue(_uiState.value?.copy(playerState = PlayerState.PREPARED))
            },
            onCompletion = {
                resetPlayer()
            }
        )
    }

    private fun updateCurrentPosition() {
        val position = playerInteractor.getCurrentPosition()
        val formattedTime = formatTime(position)
        _uiState.postValue(_uiState.value?.copy(currentPosition = formattedTime))
    }

    private fun formatTime(timeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }

    fun playbackControl() {
        when (currentState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> {} // ничего не делаем
        }
    }

    private fun startPlayer() {
        playerInteractor.playTrack()
        currentState = PlayerState.PLAYING
        _uiState.value = _uiState.value?.copy(playerState = PlayerState.PLAYING)
        handler.post(timeUpdateRunnable)
    }

    private fun pausePlayer() {
        playerInteractor.pauseTrack()
        currentState = PlayerState.PAUSED
        _uiState.value = _uiState.value?.copy(playerState = PlayerState.PAUSED)
    }

    private fun resetPlayer() {
        currentState = PlayerState.PREPARED
        _uiState.postValue(
            _uiState.value?.copy(
                playerState = PlayerState.PREPARED,
                currentPosition = "00:00"
            )
        )
    }

    fun onPause() {
        handler.removeCallbacks(timeUpdateRunnable)
        if (currentState == PlayerState.PLAYING) {
            pausePlayer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(timeUpdateRunnable)
        playerInteractor.releaseResources()
    }
}