package com.example.myplmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {
    companion object {
        @Volatile
        lateinit var sharedPreferences: SharedPreferences
        const val SETTING_KEY = "settings"
        const val HISTORY_KEY = "history_key"
        const val THEM_KEY = "them_key"
        const val SHER_PREF_KEY = "sharedPreferences"
    }

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = synchronized(this) {
            getSharedPreferences(SHER_PREF_KEY, MODE_PRIVATE)
        }

        darkTheme = sharedPreferences.getBoolean(THEM_KEY, darkTheme)
        switchTheme(darkTheme)
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
        saveTheme(darkThemeEnabled)
    }


    fun saveTheme(darkTheme: Boolean) {
        synchronized(this) {
            sharedPreferences.edit()
                .putBoolean(THEM_KEY, darkTheme)
                .putBoolean(THEM_KEY, darkTheme)
                .apply()

        }
    }
}