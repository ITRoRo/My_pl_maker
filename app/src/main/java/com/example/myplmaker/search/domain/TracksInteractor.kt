package com.example.myplmaker.search.domain


import com.example.myplmaker.search.domain.model.Track

interface TracksInteractor {

    fun searchTracks(text: String, consumer: TracksConsumer)


    fun load(): List<Track>
    fun save(trackItem : Track)

    fun clearHistory()




    interface TracksConsumer {
        fun consumer(foundTracks: List<Track>?, error: Int?)
    }
}