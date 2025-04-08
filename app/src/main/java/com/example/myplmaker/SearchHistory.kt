package com.example.myplmaker
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.myplmaker.App.Companion.sharedPreferences
import com.example.myplmaker.App.Companion.HISTORY_KEY
import java.util.ArrayList


class SearchHistory {

    private val gson = Gson()


    fun fromJson(historyTracks: ArrayList<Track>) {
        val fromJson = sharedPreferences.getString(HISTORY_KEY, null)

        if (fromJson != null) {
            val turnsType = object : TypeToken<ArrayList<Track>>() {}.type
            val prefList = gson.fromJson<ArrayList<Track>>(fromJson, turnsType)
            historyTracks.clear()
            historyTracks.addAll(prefList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun addTrackInHistory(historyTracks: ArrayList<Track>, trackItem: Track) {
        if (historyTracks.contains(trackItem)) {
            historyTracks.remove(trackItem)
        }
        if (historyTracks.size > 10) {
            historyTracks.removeLast()
        }
        historyTracks.add(0, trackItem)
        val json = gson.toJson(historyTracks)
        sharedPreferences.edit().putString("HISTORY_KEY", json).apply()
    }

    fun clearHistory(historyTracks: ArrayList<Track>) {
        historyTracks.clear()
        sharedPreferences.edit().clear().apply()
    }

}