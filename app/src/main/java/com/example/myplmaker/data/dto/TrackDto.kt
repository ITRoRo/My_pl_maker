package com.example.myplmaker.data.dto

data class TrackDto (val trackName: String, // Название композиции
                     val artistName: String, // Имя исполнителя
                     val trackTimeMillis: Long, // Продолжительность трека
                     val artworkUrl100: String,
                     val trackId: Int,// Ссылка
                     val collectionName: String?, //Альбома
                     val releaseDate: String?, //Год
                     val primaryGenreName: String?, //Жанр
                     val country: String?,
                     val previewUrl: String//Страна
)
