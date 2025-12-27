package com.example.myplmaker.search.domain

import com.example.myplmaker.search.data.model.Answers
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow


interface TracksRepository {
    fun searchTracks(text: String): Flow<Answers<List<Track>>>
}