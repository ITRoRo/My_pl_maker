package com.practicum.playlistmaker.search.data.impl

import android.content.SharedPreferences
import android.media.session.MediaSession
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myplmaker.App
import com.example.myplmaker.App.Companion
import com.example.myplmaker.App.Companion.HISTORY_KEY
import com.example.myplmaker.search.domain.model.Track

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.practicum.playlistmaker.search.data.HistoryInterface



class SearchHistory(private val sharedPreferences: SharedPreferences) : HistoryInterface {

    private val gson = Gson()

    // Ленивая инициализация истории для избежания загрузки при каждом обращении
    private val historyTracks: ArrayList<Track> by lazy {
        loadHistoryFromPrefs()
    }

    private fun loadHistoryFromPrefs(): ArrayList<Track> {
        val historyJson = sharedPreferences.getString(HISTORY_KEY, null)
        return if (historyJson != null) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            gson.fromJson(historyJson, type)
        } else {
            ArrayList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun save(track: Track) {
        // Удаляем трек, если он уже есть в истории
        historyTracks.removeIf { it.trackId == track.trackId }

        // Добавляем трек в начало списка
        historyTracks.add(0, track)

        // Ограничиваем размер истории
        if (historyTracks.size > 10) {
            historyTracks.removeAt(historyTracks.size - 1)
        }

        // Сохраняем историю
        saveToPrefs()
    }

    override fun load(): ArrayList<Track> {
        // Возвращаем копию списка, чтобы избежать изменений извне
        return ArrayList(historyTracks)
    }

    override fun clearHistory() {
        historyTracks.clear()
        // Удаляем только ключ истории, а не все настройки
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    // Вспомогательный метод для сохранения истории
    private fun saveToPrefs() {
        val json = gson.toJson(historyTracks)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }
}