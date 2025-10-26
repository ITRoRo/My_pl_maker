package com.example.myplmaker.search.data.network

import com.example.myplmaker.search.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ITunesApi {
    @GET("/search?entity=song&country=ru")
    fun searchTracks(@Query("term") text: String): Call<TrackSearchResponse>
}