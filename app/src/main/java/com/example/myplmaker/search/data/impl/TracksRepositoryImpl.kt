package com.example.myplmaker.search.data.impl

import com.example.myplmaker.search.data.dto.TrackSearchRequest
import com.example.myplmaker.search.data.dto.TrackSearchResponse
import com.example.myplmaker.search.data.model.Answers
import com.example.myplmaker.search.data.network.NetworkClient
import com.example.myplmaker.search.domain.TracksRepository
import com.example.myplmaker.search.domain.model.Track


class TracksRepositoryImpl( private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(text: String): Answers<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(text))
        return when (response.resultCode) {
            -1 -> Answers.Error(-1)
            200 -> {
                Answers.Success((response as TrackSearchResponse).results.map {
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
                    )

                })
            }

            else -> Answers.Error(400)
        }
    }
}

