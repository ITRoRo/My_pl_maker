package com.example.myplmaker.playlist.data

import com.example.myplmaker.db.convertor.PlaylistTrackDbConvertor
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.example.myplmaker.db.AppDatabase
import com.example.myplmaker.db.convertor.PlaylistDbConvertor
import com.example.myplmaker.playlist.domain.PlaylistRepository
import com.example.myplmaker.playlist.domain.model.Playlist
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackDbConvertor: PlaylistTrackDbConvertor
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao()
            .addPlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { entities ->
            entities.map { entity ->
                playlistDbConvertor.map(entity)
            }
        }
    }

    override suspend fun addTrackToPlaylistTrackTable(track: Track) {
        val trackEntity = playlistTrackDbConvertor.map(track)
        appDatabase.playlistTrackDao().insertTrack(trackEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist?> {
        return appDatabase.playlistDao().getPlaylistById(playlistId).map { playlistEntity ->
            playlistEntity?.let { playlistDbConvertor.map(it) }
        }
    }

    override fun getTracksForPlaylist(trackIds: List<Int>): Flow<List<Track>> {
        if (trackIds.isEmpty()) {
            return flowOf(emptyList())
        }
        return appDatabase.playlistTrackDao().getTracksByIds(trackIds).map { tracks ->
            tracks.map { playlistTrackDbConvertor.map(it) }
        }
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist) {
        val updatedTrackIds = playlist.trackIds.toMutableList()
        updatedTrackIds.remove(trackId)

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        appDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(updatedPlaylist))
        checkAndDeleteOrphanTrack(trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylistById(playlist.id)
        for (trackId in playlist.trackIds) {
            checkAndDeleteOrphanTrack(trackId)
        }
    }

    private suspend fun checkAndDeleteOrphanTrack(trackId: Int) {
        val allPlaylists = appDatabase.playlistDao().getAllPlaylists().first()
        val isTrackInAnyPlaylist = allPlaylists.any { playlistEntity ->
            val trackIdsInPlaylist =
                Gson().fromJson(playlistEntity.trackIds, Array<Int>::class.java).toList()
            trackIdsInPlaylist.contains(trackId)
        }

        if (!isTrackInAnyPlaylist) {
            appDatabase.playlistTrackDao().deleteTrackById(trackId)
        }
    }
}