package com.example.myplmaker.player.ui.view

import android.annotation.SuppressLint
import android.app.Application
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myplmaker.R
import com.example.myplmaker.media.ui.domain.FavoritesInteractor
import com.example.myplmaker.player.domain.PlayerInteractor
import com.example.myplmaker.player.ui.PlayerState
import com.example.myplmaker.player.ui.TrackUiState
import com.example.myplmaker.playlist.domain.PlaylistInteractor
import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale

class TitleViewModel(
                     application: Application,
                     private val playerInteractor: PlayerInteractor,
                     private val favoritesInteractor: FavoritesInteractor,
                     private val playlistInteractor: PlaylistInteractor
    ) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<TrackUiState>()
    val uiState: LiveData<TrackUiState> get() = _uiState

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage


    private lateinit var currentTrack: Track
    private var positionUpdateJob: Job? = null
    private var favoriteJob: Job? = null
    companion object {
        private const val POSITION_UPDATE_DELAY = 1000L
          }

    init {
        _uiState.value = TrackUiState(track = null)

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
        playerInteractor.releaseResources()
        favoriteJob?.cancel()
        stopPositionUpdateCoroutine()
    }

    fun initTrack(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            val playlists = playlistInteractor.getPlaylists().firstOrNull() ?: emptyList()

            val isFavorite = favoritesInteractor.isTrackFavorite(track.trackId).firstOrNull() ?: false
            currentTrack.isFavorite = isFavorite

            _uiState.postValue(
                TrackUiState(
                    track = track,
                    playerState = PlayerState.DEFAULT,
                    isFavorite = isFavorite,
                    playlists = playlists
                )
            )

            listenForFavoriteChanges()
            listenForPlaylistChanges()

            preparePlayer()
        }
    }

    private fun listenForFavoriteChanges() {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoritesInteractor.isTrackFavorite(currentTrack.trackId)
                .collect { isFavorite ->
                    _uiState.postValue(_uiState.value?.copy(isFavorite = isFavorite))
                    currentTrack.isFavorite = isFavorite
                }
        }
    }

    private fun listenForPlaylistChanges() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                _uiState.postValue(_uiState.value?.copy(playlists = playlists))
            }
        }
    }

    private fun preparePlayer() {
        playerInteractor.prepareTrack(
            currentTrack,
            onPrepared = {
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
            while (_uiState.value?.playerState == PlayerState.PLAYING) {
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
        when (_uiState.value?.playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> {}
        }
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                _uiState.postValue(_uiState.value?.copy(playlists = playlists))
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    fun onAddTrackToPlaylistClicked(playlist: Playlist) {
        viewModelScope.launch {
            val isAdded = playlistInteractor.addTrackToPlaylist(currentTrack, playlist)
            if (isAdded) {
                val message = getApplication<Application>().getString(R.string.add_in_pl_to, playlist.name)
                _toastMessage.postValue(message)
            } else {
                val message = getApplication<Application>().getString(R.string.been_added, playlist.name)
                _toastMessage.postValue(message)            }
        }
    }


    private fun startPlayer() {
        playerInteractor.playTrack()
        _uiState.value = _uiState.value?.copy(playerState = PlayerState.PLAYING)
        startPositionUpdateCoroutine()
    }

    private fun pausePlayer() {
        playerInteractor.pauseTrack()
        _uiState.value = _uiState.value?.copy(playerState = PlayerState.PAUSED)
        stopPositionUpdateCoroutine()
    }

    private fun resetPlayer() {
        stopPositionUpdateCoroutine()

        _uiState.postValue(
            _uiState.value?.copy(
                playerState = PlayerState.PREPARED,
                currentPosition = "00:00"
            )
        )
    }

    fun onPause() {
        stopPositionUpdateCoroutine()
        if (_uiState.value?.playerState == PlayerState.PLAYING) {
            pausePlayer()
        }
    }




}