package com.example.myplmaker.search.domain


import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(text: String) : Flow<Pair<List<Track>?, Int?>>
}