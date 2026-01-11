package com.example.myplmaker.playlist.domain

import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun addPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean

    fun getPlaylistById(playlistId: Int): Flow<Playlist?>

    fun getTracksForPlaylist(trackIds: List<Int>): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)
}