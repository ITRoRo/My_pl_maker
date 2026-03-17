package com.example.myplmaker.search.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myplmaker.R
import com.example.myplmaker.search.ui.SearchScreen
import com.example.myplmaker.search.ui.view.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(
                    viewModel = viewModel,
                    onTrackClick = { track ->
                        findNavController().navigate(
                            R.id.action_searchFragment_to_titleFragment,
                            Bundle().apply { putParcelable("trackObject", track) }
                        )
                    }
                )
            }
        }
    }
}




