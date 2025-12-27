package com.example.myplmaker.search.data.impl

import com.example.myplmaker.search.data.dto.TrackSearchRequest
import com.example.myplmaker.search.data.dto.TrackSearchResponse
import com.example.myplmaker.search.data.model.Answers
import com.example.myplmaker.search.data.network.NetworkClient
import com.example.myplmaker.search.domain.TracksRepository
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.myplmaker.db.AppDatabase


class TracksRepositoryImpl(private val networkClient: NetworkClient,
                           private val appDatabase: AppDatabase
) : TracksRepository {

    override fun searchTracks(text: String): Flow<Answers<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(text))
        when (response.resultCode) {
            -1 -> emit(Answers.Error(-1))
            200 -> {
                val favoriteIds = appDatabase.favoriteTrackDao().getFavoriteTrackIds()
                with(response as TrackSearchResponse) {
                    val data = response.results.map {
                        Track(
                            it.trackName.toString(), // Название композиции
                            it.artistName, // Имя исполнителя
                            it.trackTimeMillis, // Продолжительность трека
                            it.artworkUrl100,
                            it.trackId,// Ссылка
                            it.collectionName, //Альбома
                            it.releaseDate, //Год
                            it.primaryGenreName, //Жанр
                            it.country,
                            it.previewUrl
                        ).apply {
                            isFavorite = favoriteIds.contains(it.trackId)
                        }
                    }
                    emit(Answers.Success(data))
                }
            }

            else -> emit(Answers.Error(400))

        }
    }
}

