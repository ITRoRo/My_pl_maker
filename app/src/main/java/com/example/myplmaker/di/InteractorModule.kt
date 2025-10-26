package com.example.myplmaker.di

import com.example.myplmaker.player.domain.PlayerInteractor
import com.example.myplmaker.player.domain.impl.PlayerInteractorImpl
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

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get(), androidContext())
    }
    single<SettingInteractor> {
        SettingInteractorImpl(get())
    }
}