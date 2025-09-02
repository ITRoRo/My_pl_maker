package com.example.myplmaker

import com.example.myplmaker.data.network.RetrofitNetworkClient
import com.example.myplmaker.data.network.TracksRepositoryImpl
import com.example.myplmaker.domain.api.TracksInteractor
import com.example.myplmaker.domain.api.TracksRepository
import com.example.myplmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}