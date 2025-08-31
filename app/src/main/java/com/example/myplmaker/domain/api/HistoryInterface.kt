package com.example.myplmaker.domain.api

import com.example.myplmaker.domain.models.Track

interface HistoryInterface {

    fun addTrackInHistory(historyTracks: ArrayList<Track>, trackItem: Track)

    fun clearHistory(historyTracks: ArrayList<Track>)
}