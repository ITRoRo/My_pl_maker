package com.example.myplmaker.setting.ui.view

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myplmaker.setting.domain.SettingInteractor
import com.example.myplmaker.setting.domain.model.ThemeSetting

class SettingViewModel(

    private val settingsInteractor: SettingInteractor,

) : ViewModel() {

    private val themeLiveData = MutableLiveData<ThemeSetting>()
    val switchThemeLD: LiveData<ThemeSetting> = themeLiveData

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
}