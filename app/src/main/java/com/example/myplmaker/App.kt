package com.example.myplmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.myplmaker.di.dataModule
import com.example.myplmaker.di.interactorModule
import com.example.myplmaker.di.repositoryModule
import com.example.myplmaker.di.viewModelModule
import com.example.myplmaker.setting.domain.SettingInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class App : Application() {
    companion object {
        const val SETTING_KEY = "settings"
        const val HISTORY_KEY = "history_key"
        const val THEM_KEY = "them_key"
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val settingInteractor: SettingInteractor = getKoin().get()
        val themeSetting = settingInteractor.switchTheme()
        applyTheme(themeSetting.darkTheme)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}