package com.example.myplmaker.setting.domain

import com.example.myplmaker.setting.domain.model.ThemeSetting

interface SettingRepository {
    fun switchTheme() : ThemeSetting
    fun updateTheme(darkTheme: ThemeSetting)
}