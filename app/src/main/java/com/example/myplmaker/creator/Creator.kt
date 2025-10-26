package com.example.myplmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.myplmaker.App
import com.example.myplmaker.player.data.impl.PlayerRepositoryImpl
import com.example.myplmaker.player.domain.PlayerInteractor
import com.example.myplmaker.player.domain.PlayerRepository
import com.example.myplmaker.player.domain.impl.PlayerInteractorImpl
import com.example.myplmaker.search.data.impl.TracksRepositoryImpl
import com.example.myplmaker.search.domain.TracksInteractor
import com.example.myplmaker.search.domain.TracksRepository
import com.example.myplmaker.search.domain.impl.TracksInteractorImpl
import com.example.myplmaker.search.data.network.RetrofitNetworkClient

import com.example.myplmaker.setting.data.impl.SettingRepositoryImpl
import com.example.myplmaker.setting.domain.SettingInteractor
import com.example.myplmaker.setting.domain.SettingRepository
import com.example.myplmaker.setting.domain.impl.SettingInteractorImpl
import com.example.myplmaker.sharing.data.impl.SharingRepositoryImpl
import com.example.myplmaker.sharing.domain.SharingInteractor
import com.example.myplmaker.sharing.domain.SharingRepository
import com.example.myplmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    lateinit var app: Application

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerRepository())
    }

    private fun providePlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    private fun getSearchRepository(
        sharedPreferences: SharedPreferences,
        context: Context
    ): TracksRepository {
        return TracksRepositoryImpl(sharedPreferences, RetrofitNetworkClient(context))
    }

    fun getSearchInteractor(
        sharedPreferences: SharedPreferences,
        context: Context
    ): TracksInteractor {
        return TracksInteractorImpl(getSearchRepository(sharedPreferences, context))
    }

    fun getSettingsRepository(): SettingRepository {
        return SettingRepositoryImpl(app)
    }

    fun getSettingsInteractor(): SettingInteractor {
        return SettingInteractorImpl(getSettingsRepository())
    }

    private fun getSharingRepository(context: Context): SharingRepository {
        return SharingRepositoryImpl(context)
    }

    fun getSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getSharingRepository(context))
    }
}