package com.example.myplmaker.setting.ui.view

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myplmaker.setting.domain.SettingInteractor
import com.example.myplmaker.setting.domain.model.ThemeSetting
import com.example.myplmaker.sharing.domain.SharingInteractor

class SettingViewModel(

    private val settingsInteractor: SettingInteractor,
    private val sharingInteractor: SharingInteractor,

) : ViewModel() {

    private val themeLiveData = MutableLiveData<ThemeSetting>()
    fun switchThemeLD(): LiveData<ThemeSetting> = themeLiveData

    init {
        themeLiveData.value = settingsInteractor.switchTheme()
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
    fun getLiveData() : LiveData<ThemeSetting> = themeLiveData


}