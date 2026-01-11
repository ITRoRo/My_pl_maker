package com.example.myplmaker.playlist.domain

import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylistTrackTable(track: Track)

    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylistById(playlistId: Int): Flow<Playlist?>

    fun getTracksForPlaylist(trackIds: List<Int>): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)
}