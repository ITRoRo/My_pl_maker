package com.example.myplmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ITunesApi {
    @GET("/search?entity=song&country=ru")
    fun search(@Query("term") text: String): Call<TrackResponse>
}