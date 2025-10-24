package com.example.myplmaker.search.ui

import com.example.myplmaker.search.domain.model.Track


data class LiveDataSearch(val trackList : List<Track>?, val code : Int?, val historyList : List<Track>)