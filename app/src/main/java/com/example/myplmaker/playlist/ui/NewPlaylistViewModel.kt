package com.example.myplmaker.playlist.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplmaker.playlist.domain.PlaylistInteractor
import com.example.myplmaker.playlist.domain.model.Playlist
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {


    private var isEditingMode = false
    private var initialName: String = ""
    private var initialDescription: String = ""
    private var playlistName: String = ""
    private var playlistDescription: String = ""
    private var initialCoverUri: Uri? = null
    private var currentPlaylist: Playlist? = null
    private var coverUri: Uri? = null

    private val _isButtonEnabled = MutableLiveData<Boolean>(false)
    val isButtonEnabled: LiveData<Boolean> get() = _isButtonEnabled

    private val _finishScreen = SingleLiveEvent<String>()
    val finishScreen: LiveData<String> get() = _finishScreen

    private val _showConfirmDialog = SingleLiveEvent<Unit>()
    val showConfirmDialog: LiveData<Unit> get() = _showConfirmDialog

    private val _closeScreen = SingleLiveEvent<Unit>()
    val closeScreen: LiveData<Unit> get() = _closeScreen

    private val _playlistDataToRender = SingleLiveEvent<Playlist>()
    val playlistDataToRender: LiveData<Playlist> get() = _playlistDataToRender

    fun init(playlistId: Int) {
        if (playlistId != -1) {
            isEditingMode = true
            loadPlaylistData(playlistId)
        }
    }

    private fun loadPlaylistData(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId).firstOrNull()
            if (playlist != null) {
                currentPlaylist = playlist

                playlistName = playlist.name
                playlistDescription = playlist.description ?: ""
                playlist.coverImagePath?.let { coverUri = Uri.parse(it) }

                initialName = playlist.name
                initialDescription = playlist.description ?: ""
                playlist.coverImagePath?.let { initialCoverUri = Uri.parse(it) }

                _playlistDataToRender.postValue(playlist)
                _isButtonEnabled.postValue(true)
            }
        }
    }

    fun onNameChanged(name: String) {
        playlistName = name
        _isButtonEnabled.value = name.isNotBlank()
    }

    fun onDescriptionChanged(description: String) {
        playlistDescription = description
    }

    fun getCoverUri(uri: Uri) {
        this.coverUri = uri
    }

    fun onSaveButtonClicked() {
        viewModelScope.launch {
            if (isEditingMode) {
                val updatedPlaylist = currentPlaylist!!.copy(
                    name = playlistName,
                    description = playlistDescription.takeIf { it.isNotBlank() },
                    coverImagePath = coverUri?.toString() ?: currentPlaylist!!.coverImagePath
                )
                playlistInteractor.updatePlaylist(updatedPlaylist)
                _closeScreen.postValue(Unit)
            } else {
                val newPlaylist = Playlist(
                    id = 0,
                    name = playlistName,
                    description = playlistDescription.takeIf { it.isNotBlank() },
                    coverImagePath = coverUri?.toString(),
                    trackIds = emptyList(),
                    trackCount = 0
                )
                playlistInteractor.addPlaylist(newPlaylist)
                _finishScreen.postValue(newPlaylist.name)
            }
        }
    }

    fun onBackClicked() {
        if (isEditingMode) {
            val hasRealChanges = playlistName != initialName ||
                    playlistDescription != initialDescription ||
                    coverUri != initialCoverUri
            if (hasRealChanges) {
                _showConfirmDialog.value = Unit
            } else {
                _closeScreen.value = Unit
            }
        } else {
            val hasUnsavedChanges =
                coverUri != null || playlistName.isNotBlank() || playlistDescription.isNotBlank()
            if (hasUnsavedChanges) {
                _showConfirmDialog.value = Unit
            } else {
                _closeScreen.value = Unit
            }
        }
    }
}