package com.example.myplmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myplmaker.R
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.example.myplmaker.media.ui.MediaScreen
import com.example.myplmaker.media.ui.view.FavoriteViewModel
import com.example.myplmaker.media.ui.view.PlaylistsViewModel
import com.example.myplmaker.playlist.fragment.PlaylistDetailsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {
    private val favoriteViewModel: FavoriteViewModel by viewModel()
    private val playlistViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaScreen(
                    favoriteViewModel = favoriteViewModel,
                    playlistViewModel = playlistViewModel,
                    onTrackClick = { track ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_titleFragment,
                            Bundle().apply { putParcelable("trackObject", track) }
                        )
                    },
                    onCreatePlaylistClick = {
                        findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
                    },
                    onPlaylistClick = { playlist ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_playlistDetailsFragment,
                            Bundle().apply {
                                putInt(PlaylistDetailsFragment.PLAYLIST_ID, playlist.id)
                            }
                        )
                    }
                )
            }
        }
    }
}