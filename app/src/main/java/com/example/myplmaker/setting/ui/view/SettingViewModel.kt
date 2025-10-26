package com.example.myplmaker.setting.ui.view

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myplmaker.App
import com.example.myplmaker.creator.Creator
import com.example.myplmaker.setting.domain.SettingInteractor
import com.example.myplmaker.setting.domain.model.ThemeSetting
import com.example.myplmaker.sharing.domain.SharingInteractor

class SettingViewModel(

    private val settingsInteractor: SettingInteractor,
    private val sharingInteractor: SharingInteractor,
    app: Application

) : ViewModel() {

    private val themeLiveData = MutableLiveData<ThemeSetting>()
    fun switchThemeLD(): LiveData<ThemeSetting> = themeLiveData

    init {
        themeLiveData.value = ThemeSetting(settingsInteractor.switchTheme().darkTheme)
    }

    fun switchTheme(darkTheme: Boolean) {
        settingsInteractor.updateTheme(ThemeSetting(darkTheme))
        themeLiveData.value = ThemeSetting(darkTheme)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


    fun buttonShare() {
        sharingInteractor.buttonShare()
    }

    fun buttonSupport() {
        sharingInteractor.buttonSupport()
    }

    fun buttonConsent() {
        sharingInteractor.buttonConsent()
    }

    companion object {
        fun getViewModelFactory(context : Context) : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>) : T {
                    return SettingViewModel(Creator.getSettingsInteractor(), Creator.getSharingInteractor(context), Creator.app) as T
                }
            }
    }
}