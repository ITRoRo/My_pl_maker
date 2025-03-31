package com.example.myplmaker

import android.icu.text.SimpleDateFormat
import java.util.Locale

data class Track (
    var trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    var trackTimeMillis: String, // Продолжительность трека
    var artworkUrl100: String // Ссылка на изображение обложки
)
