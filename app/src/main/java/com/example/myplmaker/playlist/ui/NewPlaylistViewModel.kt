package com.example.myplmaker.playlist.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var coverUri: Uri? = null
    private var playlistName: String = ""
    private var playlistDescription: String = ""

    private val _isButtonEnabled = MutableLiveData<Boolean>(false)
    val isButtonEnabled: LiveData<Boolean> get() = _isButtonEnabled

    private val _finishScreen = SingleLiveEvent<String>()
    val finishScreen: LiveData<String> get() = _finishScreen

    private val _showConfirmDialog = SingleLiveEvent<Unit>()
    val showConfirmDialog: LiveData<Unit> get() = _showConfirmDialog

    private val _closeScreen = SingleLiveEvent<Unit>()
    val closeScreen: LiveData<Unit> get() = _closeScreen

    fun onNameChanged(name: String) {
        playlistName = name
        _isButtonEnabled.value = name.isNotBlank()
    }

    fun onDescriptionChanged(description: String) {
        playlistDescription = description
    }

    fun setCoverUri(uri: Uri?) {
        this.coverUri = uri
    }

    fun getCoverUri(): Uri? {
        return coverUri
    }

    fun createPlaylist(filePath: String?) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlistName, playlistDescription, filePath)
            _finishScreen.postValue(playlistName)
        }
    }

    fun onBackClicked() {
        if (coverUri != null || playlistName.isNotBlank() || playlistDescription.isNotBlank()) {
            _showConfirmDialog.value = Unit
        } else {
            _closeScreen.value = Unit
        }
    }
}