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
import com.example.myplmaker.playlist.domain.PlaylistInteractor
import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class TitleViewModel(private val playerInteractor: PlayerInteractor,
                     private val favoritesInteractor: FavoritesInteractor,
                     private val playlistInteractor: PlaylistInteractor
    ) : ViewModel() {

    private val _uiState = MutableLiveData<TrackUiState>()
    val uiState: LiveData<TrackUiState> = _uiState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage
    init {
        loadPlaylists()
    }

    private lateinit var currentTrack: Track
    private var currentState = PlayerState.DEFAULT
    private var positionUpdateJob: Job? = null
    private var favoriteJob: Job? = null
    companion object {
        private const val POSITION_UPDATE_DELAY = 1000L
          }


    fun onFavoriteClicked() {
        if (currentTrack.isFavorite) {
            viewModelScope.launch {
                favoritesInteractor.deleteTrack(currentTrack)
            }
        } else {
            viewModelScope.launch {
                favoritesInteractor.addTrack(currentTrack)
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        favoriteJob?.cancel()
    }

    fun initTrack(track: Track) {
        currentTrack = track
        _uiState.value = TrackUiState(track, PlayerState.DEFAULT)
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoritesInteractor.isTrackFavorite(currentTrack.trackId)
                .collect { isFavorite ->
                    _isFavorite.postValue(isFavorite)
                    currentTrack.isFavorite = isFavorite
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

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                _playlists.postValue(it)
            }
        }
    }

    fun onAddTrackToPlaylistClicked(playlist: Playlist) {
        viewModelScope.launch {
            val isAdded = playlistInteractor.addTrackToPlaylist(currentTrack, playlist)
            if (isAdded) {
                _toastMessage.postValue("Добавлено в плейлист ${playlist.name}")
            } else {
                _toastMessage.postValue("Трек уже добавлен в плейлист ${playlist.name}")
            }
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