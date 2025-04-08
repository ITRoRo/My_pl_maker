package com.example.myplmaker
import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate




class App : Application() {
    companion object {
        lateinit var sharedPreferences : SharedPreferences
        const val SETTING_KEY = "settings"
        const val HISTORY_KEY = "history_key"
        const val THEM_KEY = "them_key"
    }
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = getSharedPreferences("SParams", MODE_PRIVATE)

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
    }
}




