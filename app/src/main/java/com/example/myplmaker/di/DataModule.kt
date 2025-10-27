package com.example.myplmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.myplmaker.search.data.impl.SharedPreferencesHistoryImpl
import com.example.myplmaker.search.data.network.ITunesApi
import com.example.myplmaker.search.data.network.NetworkClient
import com.example.myplmaker.search.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.HistoryInterface
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<NetworkClient> {
        RetrofitNetworkClient(iTunesApi = get(), context = androidContext())
    }
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ITunesApi::class.java)
    }
    factory {
        Gson()
    }
    factory  {
        MediaPlayer()
    }
    single {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    single<SharedPreferences> {
        val context: Context = get()
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }
    single<HistoryInterface> {
        SharedPreferencesHistoryImpl(
            sharedPreferences = get(),
            gson = get()
        )
    }
}