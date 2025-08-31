package com.example.myplmaker.domain.api


import com.example.myplmaker.data.dto.TrackSearchResponse
import com.example.myplmaker.domain.models.Track
import retrofit2.Call

interface TracksInteractor {

    fun searchTracks(text: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consumer(foundTracks: List<Track>)
        fun onFailure(call: Call<TrackSearchResponse>, t: Throwable)
    }
}