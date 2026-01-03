package com.example.myplmaker.di

import com.example.myplmaker.media.ui.domain.FavoritesInteractor
import com.example.myplmaker.media.ui.domain.impli.FavoritesInteractorImpl
import com.example.myplmaker.player.domain.PlayerInteractor
import com.example.myplmaker.player.domain.impl.PlayerInteractorImpl
import com.example.myplmaker.playlist.domain.PlaylistInteractor
import com.example.myplmaker.playlist.domain.impl.PlaylistInteractorImpl
import com.example.myplmaker.search.domain.TracksInteractor
import com.example.myplmaker.search.domain.impl.TracksInteractorImpl
import com.example.myplmaker.setting.domain.SettingInteractor
import com.example.myplmaker.setting.domain.impl.SettingInteractorImpl
import com.example.myplmaker.sharing.domain.SharingInteractor
import com.example.myplmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), androidContext())
    }
    factory<SettingInteractor> {
        SettingInteractorImpl(get())
    }

    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    factory<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}