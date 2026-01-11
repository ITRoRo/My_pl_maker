package com.example.myplmaker.player.ui

import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track


data class TrackUiState (
    val track: Track?,
    val playerState: PlayerState = PlayerState.DEFAULT,
    val currentPosition: String = "00:00",
    val isFavorite: Boolean = false,
    val playlists: List<Playlist> = emptyList()
)

