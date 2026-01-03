package com.example.myplmaker.playlist.domain

import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(name: String, description: String?, imagePath: String?)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylistTrackTable(track: Track)
    suspend fun updatePlaylist(playlist: Playlist)
}