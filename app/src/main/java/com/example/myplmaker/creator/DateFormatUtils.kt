package com.example.myplmaker.creator

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateFormatUtils {
    fun formatReleaseDate(dateString: String): String? {
        return try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = originalFormat.parse(dateString)
            date?.let {
                SimpleDateFormat("yyyy", Locale.getDefault()).format(it)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }
}