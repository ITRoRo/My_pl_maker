package com.example.myplmaker.search.data.impl

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi


import com.example.myplmaker.search.data.dto.TrackSearchRequest
import com.example.myplmaker.search.data.dto.TrackSearchResponse
import com.example.myplmaker.search.data.model.Answers
import com.example.myplmaker.search.data.network.NetworkClient
import com.example.myplmaker.search.domain.TracksRepository
import com.example.myplmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.data.impl.SearchHistory


class TracksRepositoryImpl(sharedPreferences: SharedPreferences, private val networkClient: NetworkClient) : TracksRepository {

    private val history = SearchHistory(sharedPreferences)


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

    override fun load(): List<Track> {
        return history.load()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun save(trackItem: Track) {
        history.save(trackItem)
    }

    override fun clearHistory() {
        history.clearHistory()
    }
}

