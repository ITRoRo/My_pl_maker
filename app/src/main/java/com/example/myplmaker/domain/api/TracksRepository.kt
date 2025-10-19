package com.example.myplmaker.domain.api

import com.example.myplmaker.domain.models.Track


interface TracksRepository {
    fun searchTrack(text: String): List<Track>

}