package com.example.myplmaker.media.ui.view

import com.example.myplmaker.playlist.domain.model.Playlist

sealed class PlaylistsScreenState {
    object Empty : PlaylistsScreenState()
    data class Content(val playlists: List<Playlist>) : PlaylistsScreenState()
}