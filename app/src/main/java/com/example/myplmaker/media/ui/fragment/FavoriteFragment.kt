package com.example.myplmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myplmaker.R
import com.example.myplmaker.databinding.FragmentFavoriteBinding
import com.example.myplmaker.media.ui.model.FavoritesState
import com.example.myplmaker.media.ui.view.FavoriteViewModel
import com.example.myplmaker.search.domain.model.Track
import com.example.myplmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteViewModel by viewModel()
    private var trackAdapter: TrackAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.loadFavoriteTracks()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavoriteTracks()
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter()
        binding.mediaRecyclerView.adapter = trackAdapter
        trackAdapter?.onItemClick = { track ->
            findNavController().navigate(
                R.id.action_mediaFragment_to_titleFragment,
                Bundle().apply { putParcelable("trackObject", track) }
            )
        }
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.tracks)
            is FavoritesState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.mediaRecyclerView.isVisible = false
        binding.imageBox.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        binding.imageBox.isVisible = false
        binding.mediaRecyclerView.isVisible = true
        trackAdapter?.tracks = tracks.toMutableList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        trackAdapter = null
        _binding = null
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }
}