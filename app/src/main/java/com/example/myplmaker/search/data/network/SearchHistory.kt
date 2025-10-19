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



class SearchHistory(private val sharedPreferences: SharedPreferences) :
    HistoryInterface {

    private val gson = sharedPreferences.getString(HISTORY_KEY, Gson().toJson(null))
    class Token : TypeToken<ArrayList<Track>>()
    private val historyTracks : ArrayList<Track> = if(gson == Gson().toJson(null)) ArrayList() else Gson().fromJson(gson, Token().type)

    /*  private fun fromJson(): ArrayList<Track> {
        val fromJson = sharedPreferences.getString(HISTORY_KEY, null)
        return if (fromJson != null) {
            val turnsType = object : TypeToken<java.util.ArrayList<Track>>() {}.type
            gson.fromJson(fromJson, turnsType)
        } else {
            arrayListOf()
        }

    }*/
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun save(track: Track) {
        if (historyTracks.contains(track)) {
            historyTracks.remove(track)
        }
        if (historyTracks.size > 10) {
            historyTracks.removeLast()
        }

        val json = Gson().toJson(historyTracks)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }

    override fun load(): ArrayList<Track> {
        return historyTracks
    }

    override fun clearHistory() {

        historyTracks.clear()
        sharedPreferences.edit().clear().apply()
    }


}