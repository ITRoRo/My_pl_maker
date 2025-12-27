package com.example.myplmaker.player.ui.view

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplmaker.media.ui.domain.FavoritesInteractor
import com.example.myplmaker.player.domain.PlayerInteractor
import com.example.myplmaker.player.ui.PlayerState
import com.example.myplmaker.player.ui.TrackUiState
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class TitleViewModel(private val playerInteractor: PlayerInteractor,  private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val _uiState = MutableLiveData<TrackUiState>()
    val uiState: LiveData<TrackUiState> = _uiState
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private lateinit var currentTrack: Track
    private var currentState = PlayerState.DEFAULT
    private var positionUpdateJob: Job? = null

    companion object {
        private const val POSITION_UPDATE_DELAY = 1000L
          }


    fun onFavoriteClicked() {
        val favorite = _isFavorite.value ?: false
        if (favorite) {
            viewModelScope.launch {
                favoritesInteractor.deleteTrack(currentTrack)
            }
        } else {
            viewModelScope.launch {
                favoritesInteractor.addTrack(currentTrack)
            }
        }
        _isFavorite.value = !favorite
        currentTrack.isFavorite = !favorite
    }

    fun initTrack(track: Track) {
        currentTrack = track
        _uiState.value = TrackUiState(track, PlayerState.DEFAULT)
        _isFavorite.value = track.isFavorite
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

    private fun startPositionUpdateCoroutine() {
        stopPositionUpdateCoroutine()

        positionUpdateJob = viewModelScope.launch {
            while (currentState == PlayerState.PLAYING) {
                updateCurrentPosition()
                delay(POSITION_UPDATE_DELAY)
            }
        }
    }

    private fun stopPositionUpdateCoroutine() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
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
            else -> {}
        }
    }

    private fun startPlayer() {
        playerInteractor.playTrack()
        currentState = PlayerState.PLAYING
        _uiState.value = _uiState.value?.copy(playerState = PlayerState.PLAYING)
        startPositionUpdateCoroutine()
    }

    private fun pausePlayer() {
        playerInteractor.pauseTrack()
        currentState = PlayerState.PAUSED
        _uiState.value = _uiState.value?.copy(playerState = PlayerState.PAUSED)
        stopPositionUpdateCoroutine()
    }

    private fun resetPlayer() {
        stopPositionUpdateCoroutine()

        currentState = PlayerState.PREPARED
        _uiState.postValue(
            _uiState.value?.copy(
                playerState = PlayerState.PREPARED,
                currentPosition = "00:00"
            )
        )
    }

    fun onPause() {
        stopPositionUpdateCoroutine()
        if (currentState == PlayerState.PLAYING) {
            pausePlayer()
        }
    }


}