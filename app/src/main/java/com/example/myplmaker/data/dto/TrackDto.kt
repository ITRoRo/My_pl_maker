package com.example.myplmaker.data.dto

data class TrackDto (var trackName: String, // Название композиции
                     var artistName: String, // Имя исполнителя
                     var trackTimeMillis: Long, // Продолжительность трека
                     var artworkUrl100: String,
                     val trackId: Int,// Ссылка
                     val collectionName: String?, //Альбома
                     val releaseDate: String?, //Год
                     val primaryGenreName: String?, //Жанр
                     val country: String?,
                     val previewUrl: String//Страна
)
