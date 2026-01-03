package com.example.myplmaker.media.ui.domain.impli

import com.example.myplmaker.media.ui.domain.FavoritesInteractor
import com.example.myplmaker.media.ui.domain.FavoritesRepository
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {
    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override fun isTrackFavorite(trackId: Int): Flow<Boolean> {
        return repository.isTrackFavorite(trackId)
    }
}