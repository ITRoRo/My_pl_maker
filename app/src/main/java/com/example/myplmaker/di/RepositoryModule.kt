package com.example.myplmaker.di

import com.example.myplmaker.db.convertor.TrackDbConvertor
import com.example.myplmaker.media.ui.data.FavoritesRepositoryImpl
import com.example.myplmaker.media.ui.domain.FavoritesRepository
import com.example.myplmaker.player.data.impl.PlayerRepositoryImpl
import com.example.myplmaker.player.domain.PlayerRepository
import com.example.myplmaker.search.data.impl.TracksRepositoryImpl
import com.example.myplmaker.search.domain.TracksRepository
import com.example.myplmaker.setting.data.impl.SettingRepositoryImpl
import com.example.myplmaker.setting.domain.SettingRepository
import com.example.myplmaker.sharing.data.impl.SharingRepositoryImpl
import com.example.myplmaker.sharing.domain.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    factory<SettingRepository> {
        SettingRepositoryImpl(get())
    }

    factory<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }

    factory { TrackDbConvertor() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
}