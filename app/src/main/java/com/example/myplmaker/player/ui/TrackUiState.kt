package com.example.myplmaker.player.ui

import com.example.myplmaker.search.domain.model.Track


data class TrackUiState (
    val track: Track,
    val playerState: PlayerState,
    val currentPosition: String = "00:00"
)

