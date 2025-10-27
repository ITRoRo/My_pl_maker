package com.example.myplmaker.search.domain.impl


import com.example.myplmaker.search.data.model.Answers
import com.example.myplmaker.search.domain.TracksInteractor
import com.example.myplmaker.search.domain.TracksRepository
import com.example.myplmaker.search.domain.model.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, consumer: TracksInteractor.TracksConsumer) {

        executor.execute {
            when (val stateType: Answers<List<Track>> = repository.searchTracks(text)) {
                is Answers.Success -> {
                    consumer.consumer(stateType.data, null)
                }

                else -> {
                    consumer.consumer(null, stateType.answer)
                }
            }
        }
    }


}