package com.example.myplmaker.data.network

import com.example.myplmaker.data.NetworkClient
import com.example.myplmaker.data.dto.TrackSearchRequest
import com.example.myplmaker.data.dto.TrackSearchResponse
import com.example.myplmaker.domain.api.TracksRepository
import com.example.myplmaker.domain.models.Track


class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTrack(text: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(text))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
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
                    it.previewUrl) }
        } else {
            return emptyList()
        }
    }
}
