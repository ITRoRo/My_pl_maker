package com.example.myplmaker.playlist.domain.impl

import com.example.myplmaker.playlist.domain.PlaylistInteractor
import com.example.myplmaker.playlist.domain.PlaylistRepository
import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull


class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist) {
        repository.deleteTrackFromPlaylist(trackId, playlist)
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {

        val freshPlaylist = repository.getPlaylistById(playlist.id).firstOrNull() ?: return false


        if (freshPlaylist.trackIds.contains(track.trackId)) {             return false
        }

        repository.addTrackToPlaylistTrackTable(track)

        val updatedTrackIds = freshPlaylist.trackIds.toMutableList().apply {
            add(0, track.trackId)
        }
        val updatedPlaylist = freshPlaylist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        repository.updatePlaylist(updatedPlaylist)
        return true
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist?> {
        return repository.getPlaylistById(playlistId)
    }

    override fun getTracksForPlaylist(trackIds: List<Int>): Flow<List<Track>> {
        return repository.getTracksForPlaylist(trackIds)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }
}

