package com.example.myplmaker.setting.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myplmaker.R
import com.example.myplmaker.databinding.ActivitySettingsBinding
import com.example.myplmaker.setting.ui.view.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingViewModel by viewModel {
        parametersOf(this@SettingActivity)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.switchThemeLD().observe(this) { themeSetting ->
            binding.themeSwitcher.isChecked = themeSetting.darkTheme
        }

        binding.themeSwitcher.setOnCheckedChangeListener {_, isChecked ->
            viewModel.switchTheme(isChecked)
        }

        binding.back.setOnClickListener { finish() }

        binding.themeSwitcher.apply {
            viewModel.switchThemeLD().observe(this@SettingActivity) {
                isChecked = it.darkTheme
                setOnCheckedChangeListener { _, _ ->
                    viewModel.switchTheme(isChecked)
                }
            }
        }

        binding.share.setOnClickListener {
            viewModel.buttonShare()
        }

        binding.consent.setOnClickListener {
            viewModel.buttonConsent()
        }

        binding.support.setOnClickListener {
            viewModel.buttonSupport()
        }
    }
}