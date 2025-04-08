package com.example.myplmaker



data class Track (
    var trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    var trackTimeMillis: String, // Продолжительность трека
    var artworkUrl100: String,
    val trackId : String// Ссылка на изображение обложки
)
