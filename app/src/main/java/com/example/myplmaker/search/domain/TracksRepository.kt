package com.example.myplmaker.search.domain

import com.example.myplmaker.search.data.model.Answers
import com.example.myplmaker.search.domain.model.Track


interface TracksRepository {
    fun searchTracks(text: String): Answers<List<Track>>
}