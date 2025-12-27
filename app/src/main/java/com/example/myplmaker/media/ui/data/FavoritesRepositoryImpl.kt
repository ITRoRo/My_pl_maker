package com.example.myplmaker.media.ui.data

import com.example.myplmaker.db.AppDatabase
import com.example.myplmaker.db.convertor.TrackDbConvertor
import com.example.myplmaker.media.ui.domain.FavoritesRepository
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConvertor
) : FavoritesRepository {

    override suspend fun addTrack(track: Track) {
        appDatabase.favoriteTrackDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.favoriteTrackDao().deleteTrack(trackDbConverter.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTrackDao().getFavoriteTracks().map { tracks ->
            tracks.map { trackEntity ->
                trackDbConverter.map(trackEntity).apply { isFavorite = true }
            }
        }
    }
}