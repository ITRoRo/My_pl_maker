package com.example.myplmaker.search.ui.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplmaker.search.DELAY.CLICK_DEBOUNCE_DELAY
import com.example.myplmaker.search.DELAY.SEARCH_DEBOUNCE_DELAY
import com.example.myplmaker.search.domain.TracksInteractor
import com.example.myplmaker.search.domain.model.Track
import com.example.myplmaker.search.ui.LiveDataSearch
import com.practicum.playlistmaker.search.data.HistoryInterface
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: TracksInteractor,
    private val historyInteractor: HistoryInterface
) : ViewModel() {

    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    private val _state = MutableLiveData<LiveDataSearch>()
    fun observeState(): LiveData<LiveDataSearch> = _state

    private var lastFoundTracks: List<Track> = emptyList()
    fun searchDebounce(expression: String) {
        if (expression == latestSearchText && expression.isNotBlank()) {
            return
        }
        latestSearchText = expression
        searchJob?.cancel()

        if (expression.isBlank()) {
            load()
            return
        }
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(expression)
        }
    }

    private fun searchRequest(newExpression: String) {
        if (newExpression.isNotEmpty()) {
            viewModelScope.launch {
                searchInteractor
                    .searchTracks(newExpression)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorCode: Int?) {
        val tracks = foundTracks ?: emptyList()
        when {
            errorCode != null -> {
                _state.postValue(LiveDataSearch(listOf(), -1, listOf()))
            }
            else -> {
                lastFoundTracks = tracks
                _state.postValue(LiveDataSearch(tracks, 200, listOf()))
            }
        }
    }


    fun load() {
        searchJob?.cancel()
        _state.postValue(LiveDataSearch(listOf(), -2, historyInteractor.load()))
    }

    fun save(trackItem: Track) {
        historyInteractor.save(trackItem)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        load()
    }

    private var isClickAllowed = true
    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}



