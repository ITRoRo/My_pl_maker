package com.example.myplmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.myplmaker.creator.Creator


class App : Application() {
    companion object {

        const val SETTING_KEY = "settings"
        const val HISTORY_KEY = "history_key"
        const val THEM_KEY = "them_key"
    }

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        Creator.app = this

        val settingInteractor = Creator.getSettingsInteractor()
        val themeSetting = settingInteractor.switchTheme()

        switchTheme(themeSetting.darkTheme)
    }



    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }



}