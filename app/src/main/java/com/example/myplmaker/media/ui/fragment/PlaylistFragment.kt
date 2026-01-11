package com.example.myplmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myplmaker.R
import com.example.myplmaker.databinding.FragmentPlayBinding
import com.example.myplmaker.media.ui.view.PlaylistsScreenState
import com.example.myplmaker.media.ui.view.PlaylistsViewModel
import com.example.myplmaker.playlist.ui.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()
    private var playlistAdapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistAdapter = PlaylistAdapter()
        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = playlistAdapter

        binding.updateButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    private fun render(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Empty -> {
                binding.playlistsRecyclerView.isVisible = false
                binding.placeholderEmpty.isVisible = true
            }
            is PlaylistsScreenState.Content -> {
                binding.playlistsRecyclerView.isVisible = true
                binding.placeholderEmpty.isVisible = false
                playlistAdapter?.updateData(state.playlists)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playlistsRecyclerView.adapter = null
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistFragment().apply {}
    }
}