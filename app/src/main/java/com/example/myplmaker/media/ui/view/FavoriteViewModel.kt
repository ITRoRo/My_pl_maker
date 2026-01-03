package com.example.myplmaker.media.ui.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplmaker.media.ui.domain.FavoritesInteractor
import com.example.myplmaker.media.ui.model.FavoritesState
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> get() = _state



    fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            _state.postValue(FavoritesState.Empty)
        } else {
            _state.postValue(FavoritesState.Content(tracks))
        }
    }
}