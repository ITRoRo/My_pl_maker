package com.example.myplmaker.playlist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _uiState = MutableLiveData<PlaylistDetailsUiState>()
    val uiState: LiveData<PlaylistDetailsUiState> get() = _uiState

    private val _playlistDeleted = SingleLiveEvent<Unit>()
    val playlistDeleted: LiveData<Unit> get() = _playlistDeleted

    fun loadPlaylistDetails(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId)
                .flatMapLatest { playlist ->
                    val trackIds = playlist?.trackIds ?: emptyList()
                    playlistInteractor.getTracksForPlaylist(trackIds)
                        .map { tracks ->
                            val tracksById = tracks.associateBy { it.trackId }
                            val sortedTracks = trackIds.mapNotNull { id -> tracksById[id] }
                            val totalDuration = sortedTracks.sumOf { it.trackTimeMillis }
                            val totalMinutes = if (totalDuration == 0L) 0 else SimpleDateFormat(
                                "mm",
                                Locale.getDefault()
                            ).format(totalDuration).toInt()

                            PlaylistDetailsUiState(
                                playlist = playlist,
                                tracks = sortedTracks,
                                totalDurationMinutes = totalMinutes
                            )
                        }
                }.collect { state ->
                    _uiState.postValue(state)
                }
        }
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            val currentPlaylist = _uiState.value?.playlist ?: return@launch
            playlistInteractor.deleteTrackFromPlaylist(trackId, currentPlaylist)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            val currentPlaylist = _uiState.value?.playlist ?: return@launch
            playlistInteractor.deletePlaylist(currentPlaylist)
            _playlistDeleted.postValue(Unit)
        }
    }
}