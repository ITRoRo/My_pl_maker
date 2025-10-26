package com.practicum.playlistmaker.search.data

import com.example.myplmaker.search.domain.model.Track

interface HistoryInterface {

    fun save(track: Track)

    fun load(): ArrayList<Track>

    fun clearHistory()
}