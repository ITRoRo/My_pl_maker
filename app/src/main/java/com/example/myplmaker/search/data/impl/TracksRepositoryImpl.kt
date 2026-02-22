package com.example.myplmaker.search.data.impl

import com.example.myplmaker.search.data.dto.TrackSearchRequest
import com.example.myplmaker.search.data.dto.TrackSearchResponse
import com.example.myplmaker.search.data.model.Answers
import com.example.myplmaker.search.data.network.NetworkClient
import com.example.myplmaker.search.domain.TracksRepository
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.myplmaker.db.dao.TrackDao

class TracksRepositoryImpl(private val networkClient: NetworkClient,
                           private val trackDao: TrackDao
) : TracksRepository {

    override fun searchTracks(text: String): Flow<Answers<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(text))
        when (response.resultCode) {
            -1 -> emit(Answers.Error(-1))
            200 -> {

                with(response as TrackSearchResponse) {
                    val data = response.results.map {
                        Track(
                            it.trackName.toString(),
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.trackId,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Answers.Success(data))
                }
            }

            else -> emit(Answers.Error(400))

        }
    }
}

