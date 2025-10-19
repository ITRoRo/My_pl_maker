package com.example.myplmaker.setting.data.impl

import android.content.Context
import android.content.SharedPreferences
import com.example.myplmaker.App.Companion.SETTING_KEY
import com.example.myplmaker.App.Companion.THEM_KEY
import com.example.myplmaker.setting.domain.SettingRepository
import com.example.myplmaker.setting.domain.model.ThemeSetting

class SettingRepositoryImpl(
    private val context : Context
) : SettingRepository {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SETTING_KEY, Context.MODE_PRIVATE)

    override fun switchTheme(): ThemeSetting {
        val darkTheme = sharedPreferences.getBoolean(THEM_KEY, false)
        return ThemeSetting(darkTheme)
    }
    override fun updateTheme(checked: ThemeSetting) {
        sharedPreferences.edit()
            .putBoolean(THEM_KEY, checked.darkTheme)
            .apply()
    }
}