package com.example.myplmaker.playlist.ui

import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track

data class PlaylistDetailsUiState(
    val playlist: Playlist?,
    val tracks: List<Track>,
    val totalDurationMinutes: Int
)
