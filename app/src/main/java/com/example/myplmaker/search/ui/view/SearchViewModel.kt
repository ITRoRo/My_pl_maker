package com.example.myplmaker.search.ui.view

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myplmaker.search.domain.TracksInteractor
import com.example.myplmaker.search.domain.model.Track
import com.example.myplmaker.search.ui.LiveDataSearch
import com.practicum.playlistmaker.search.data.HistoryInterface


class SearchViewModel(private val searchInteractor: TracksInteractor, private val historyInteractor : HistoryInterface) : ViewModel() {
    var searchText: String = ""



    private val liveData = MutableLiveData<LiveDataSearch>()

    private val consumer = object : TracksInteractor.TracksConsumer {
        override fun consumer(foundTracks: List<Track>?, error: Int?) {
            liveData.postValue(LiveDataSearch(foundTracks, error, historyInteractor.load()))
        }
    }

    fun search(text: String) {
        searchInteractor.searchTracks(text, consumer)
    }

    fun getResult(): LiveData<LiveDataSearch> = liveData

    fun load() {
        liveData.postValue(LiveDataSearch(listOf(), -2, historyInteractor.load()))
    }

    fun save(trackItem: Track) {
        historyInteractor.save(trackItem)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
    }


}