package com.example.myplmaker.playlist.domain.model

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String?,
    val coverImagePath: String?,
    var trackIds: List<Int>,
    var trackCount: Int
)
