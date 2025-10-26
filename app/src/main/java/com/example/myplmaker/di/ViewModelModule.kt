package com.example.myplmaker.di

import com.example.myplmaker.player.ui.view.TitleViewModel
import com.example.myplmaker.search.ui.view.SearchViewModel
import com.example.myplmaker.setting.ui.view.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        TitleViewModel(get())
    }

    viewModel {
        SettingViewModel(
            settingsInteractor = get(),
            sharingInteractor = get()
        )
    }

    viewModel {
        SearchViewModel(get(), get())
    }
}