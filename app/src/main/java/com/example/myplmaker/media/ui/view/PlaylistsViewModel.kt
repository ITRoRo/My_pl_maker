package com.example.myplmaker.media.ui.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsScreenState>()
    val state: LiveData<PlaylistsScreenState> get() = _state


    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    _state.postValue(PlaylistsScreenState.Empty)
                } else {
                    _state.postValue(PlaylistsScreenState.Content(playlists))
                }
            }
        }
    }
}