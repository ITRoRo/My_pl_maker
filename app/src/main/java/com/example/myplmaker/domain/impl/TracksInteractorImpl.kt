package com.example.myplmaker.domain.impl

import com.example.myplmaker.domain.api.TracksInteractor
import com.example.myplmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl (private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consumer(repository.searchTrack(text))
        }
    }
}