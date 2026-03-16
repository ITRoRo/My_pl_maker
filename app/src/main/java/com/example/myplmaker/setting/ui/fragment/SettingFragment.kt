package com.example.myplmaker.setting.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.myplmaker.R
import com.example.myplmaker.setting.ui.view.SettingViewModel
import com.example.myplmaker.settings.ui.SettingsScreen

import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen(
                    viewModel = viewModel,
                    onShareClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
                        }
                        startActivity(Intent.createChooser(intent, null))
                    },
                    onSupportClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.account)))
                            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.thanks_message))
                        }
                        startActivity(intent)
                    },
                    onAgreementClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.email)))
                        startActivity(intent)
                    }
                )
            }
        }
    }
}