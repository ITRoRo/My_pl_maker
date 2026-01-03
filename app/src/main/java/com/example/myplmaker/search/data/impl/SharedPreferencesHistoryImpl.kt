package com.example.myplmaker.search.data.impl

import android.content.SharedPreferences
import com.example.myplmaker.db.AppDatabase
import com.example.myplmaker.search.domain.model.Track
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.HistoryInterface
import kotlinx.coroutines.runBlocking

class SharedPreferencesHistoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
) : HistoryInterface {

    companion object {
        private const val HISTORY_KEY = "search_history_tracks"
        private const val MAX_HISTORY_SIZE = 10
    }

    override fun load(): ArrayList<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, "[]") ?: "[]"
        return try {
            val array = gson.fromJson(json, Array<Track>::class.java)


            val updatedHistory = ArrayList(array.map { track ->
                track
            })
            updatedHistory
        } catch (e: Exception) {
            ArrayList()
        }
    }

    override fun save(trackItem: Track) {
        val history = load()
        history.removeAll { it.trackId == trackItem.trackId }
        history.add(0, trackItem)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }

        val json = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }
}