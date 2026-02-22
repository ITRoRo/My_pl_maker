package com.example.myplmaker.playlist.data

import com.example.myplmaker.db.convertor.PlaylistTrackDbConvertor
import com.example.myplmaker.db.dao.PlaylistDao
import com.example.myplmaker.db.dao.PlaylistTrackDao
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import com.example.myplmaker.db.AppDatabase
import com.example.myplmaker.db.convertor.PlaylistDbConvertor
import com.example.myplmaker.db.entity.PlaylistEntity
import com.example.myplmaker.playlist.domain.PlaylistRepository
import com.example.myplmaker.playlist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackDbConvertor: PlaylistTrackDbConvertor
) : PlaylistRepository {



    override suspend fun addPlaylist(name: String, description: String?, imagePath: String?) {
        val playlistEntity = PlaylistEntity(
            name = name,
            description = description,
            coverImagePath = imagePath,
            trackIds = "[]",
            trackCount = 0
        )
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
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

}