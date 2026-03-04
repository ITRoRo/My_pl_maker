package com.example.myplmaker.player.ui.view

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myplmaker.R
import com.example.myplmaker.media.ui.domain.FavoritesInteractor
import com.example.myplmaker.player.service.AudioPlayerControl
import com.example.myplmaker.player.ui.PlayerState
import com.example.myplmaker.player.ui.TrackUiState
import com.example.myplmaker.playlist.domain.PlaylistInteractor
import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TitleViewModel(
    application: Application,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<TrackUiState>()
    val uiState: LiveData<TrackUiState> get() = _uiState

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage


    private lateinit var currentTrack: Track
    private var favoriteJob: Job? = null


    private var audioPlayerControl: AudioPlayerControl? = null
    private var playerStateJob: Job? = null
    private var timerJob: Job? = null

    init {
        _uiState.value = TrackUiState(track = null)

    }


    fun setAudioPlayerControl(control: AudioPlayerControl) {
        this.audioPlayerControl = control

        playerStateJob = viewModelScope.launch {
            control.getPlayerState().collect { state ->
                _uiState.postValue(_uiState.value?.copy(playerState = state))
            }
        }

        timerJob = viewModelScope.launch {
            control.getCurrentPosition().collect { time ->
                _uiState.postValue(_uiState.value?.copy(currentPosition = time))
            }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
        playerStateJob?.cancel()
        timerJob?.cancel()
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

        audioPlayerControl = null
    }

    fun initTrack(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            val playlists = playlistInteractor.getPlaylists().firstOrNull() ?: emptyList()

            val isFavorite =
                favoritesInteractor.isTrackFavorite(track.trackId).firstOrNull() ?: false
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


    fun playbackControl() {
        val control = audioPlayerControl ?: return
        val currentState = _uiState.value?.playerState ?: PlayerState.DEFAULT

        when (currentState) {
            PlayerState.PLAYING -> control.pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> control.startPlayer()
            else -> {}
        }
    }


    @SuppressLint("StringFormatInvalid")
    fun onAddTrackToPlaylistClicked(playlist: Playlist) {
        viewModelScope.launch {
            val isAdded = playlistInteractor.addTrackToPlaylist(currentTrack, playlist)
            if (isAdded) {
                val message =
                    getApplication<Application>().getString(R.string.add_in_pl_to, playlist.name)
                _toastMessage.postValue(message)
            } else {
                val message =
                    getApplication<Application>().getString(R.string.been_added, playlist.name)
                _toastMessage.postValue(message)
            }
        }
    }


    fun onPause() {
        val state = _uiState.value?.playerState
        if (state == PlayerState.PLAYING) {
            audioPlayerControl?.showNotification()
        }
    }

    fun onResume() {
        audioPlayerControl?.hideNotification()
    }


}