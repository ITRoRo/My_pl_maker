package com.example.myplmaker.search.domain


import com.example.myplmaker.search.domain.model.Track

interface TracksInteractor {

    fun searchTracks(text: String, consumer: TracksConsumer)
    interface TracksConsumer {
        fun consumer(foundTracks: List<Track>?, error: Int?)
    }
}