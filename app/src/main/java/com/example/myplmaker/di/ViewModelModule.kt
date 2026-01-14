package com.example.myplmaker.di

import com.example.myplmaker.media.ui.view.FavoriteViewModel
import com.example.myplmaker.media.ui.view.MediaViewModel
import com.example.myplmaker.media.ui.view.PlaylistsViewModel
import com.example.myplmaker.player.ui.view.TitleViewModel
import com.example.myplmaker.playlist.ui.NewPlaylistViewModel
import com.example.myplmaker.playlist.ui.PlaylistDetailsViewModel
import com.example.myplmaker.search.ui.view.SearchViewModel
import com.example.myplmaker.setting.ui.view.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        TitleViewModel(get(), get(), get(), get())
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
    viewModel {
        MediaViewModel()
    }

    viewModel {
        FavoriteViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel {
        PlaylistDetailsViewModel(get())
    }
}