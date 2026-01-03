package com.example.myplmaker.setting.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myplmaker.R
import com.example.myplmaker.databinding.FragmentSettingsBinding
import com.example.myplmaker.setting.ui.view.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel by viewModel<SettingViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view : View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.switchThemeLD().observe(viewLifecycleOwner) { themeSetting ->
            binding.themeSwitcher.isChecked = themeSetting.darkTheme
        }

        binding.themeSwitcher.setOnCheckedChangeListener {_, isChecked ->
            viewModel.switchTheme(isChecked)
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