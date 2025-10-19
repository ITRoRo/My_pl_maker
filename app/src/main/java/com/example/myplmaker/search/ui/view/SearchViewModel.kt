package com.example.myplmaker.search.ui.view

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myplmaker.creator.Creator
import com.example.myplmaker.search.domain.TracksInteractor
import com.example.myplmaker.search.domain.model.Track
import com.example.myplmaker.search.ui.LiveDataSearch


class SearchViewModel(private val searchInteractor: TracksInteractor) : ViewModel() {
    var searchText: String = ""

    companion object {
        fun getViewModelFactory(
            sharedPreferences: SharedPreferences,
            context: Context
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        Creator.getSearchInteractor(
                            sharedPreferences,
                            context
                        )
                    ) as T
                }
            }
    }

    private val liveData = MutableLiveData<LiveDataSearch>()

    private val consumer = object : TracksInteractor.TracksConsumer {
        override fun consumer(foundTracks: List<Track>?, error: Int?) {
            liveData.postValue(LiveDataSearch(foundTracks, error, searchInteractor.load()))
        }
    }

    fun search(text: String) {
        searchInteractor.searchTracks(text, consumer)
    }

    fun getResult(): LiveData<LiveDataSearch> = liveData

    fun load() {
        liveData.postValue(LiveDataSearch(listOf(), -2, searchInteractor.load()))
    }

    fun save(trackItem: Track) {
        searchInteractor.save(trackItem)
    }

    fun clearHistory() {
        searchInteractor.clearHistory()
    }


}