package com.example.myplmaker.search.domain.impl


import com.example.myplmaker.search.data.model.Answers
import com.example.myplmaker.search.domain.TracksInteractor
import com.example.myplmaker.search.domain.TracksRepository
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {


    override fun searchTracks(text: String) : Flow<Pair<List<Track>?, Int?>> {
        return repository.searchTracks(text).map { result ->
            when (result) {
                is Answers.Success -> {
                    Pair(result.data, null)
                }

                is Answers.Error -> {
                    Pair(null, result.answer)
                }
            }
        }
    }
    }