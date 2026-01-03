package com.example.myplmaker.media.ui.model

import com.example.myplmaker.search.domain.model.Track

sealed interface FavoritesState {
    object Empty : FavoritesState
    data class Content(val tracks: List<Track>) : FavoritesState
}