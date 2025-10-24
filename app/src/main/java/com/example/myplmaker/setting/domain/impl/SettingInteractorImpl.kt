package com.example.myplmaker.setting.domain.impl

import com.example.myplmaker.setting.domain.SettingInteractor
import com.example.myplmaker.setting.domain.SettingRepository
import com.example.myplmaker.setting.domain.model.ThemeSetting

class SettingInteractorImpl(private val repositorySetting: SettingRepository) : SettingInteractor {
    override fun switchTheme() : ThemeSetting {return repositorySetting.switchTheme()}
    override fun updateTheme(darkTheme: ThemeSetting) {repositorySetting.updateTheme(darkTheme)}
}