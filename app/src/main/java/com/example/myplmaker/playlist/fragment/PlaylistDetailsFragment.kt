package com.example.myplmaker.playlist.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myplmaker.R
import com.example.myplmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.playlist.ui.PlaylistDetailsUiState
import com.example.myplmaker.playlist.ui.PlaylistDetailsViewModel
import com.example.myplmaker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistDetailsViewModel by viewModel()
    private var currentPlaylistId: Int = -1
    private var trackAdapter: TrackAdapter? = null
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPlaylistId = requireArguments().getInt(PLAYLIST_ID)
        viewModel.loadPlaylistDetails(currentPlaylistId)

        setupAdapters()
        setupBottomSheet()
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.shareButton.setOnClickListener { sharePlaylist() }

        binding.menuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.menuSheetContent.menuShare.setOnClickListener {
            sharePlaylist()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.menuSheetContent.menuEdit.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val bundle = Bundle().apply {
                putInt(NewPlaylistFragment.PLAYLIST_ID_KEY, currentPlaylistId)
            }
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_newPlaylistFragment,
                bundle
            )
        }
        binding.menuSheetContent.menuDelete.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter()

        trackAdapter?.onItemClick = { track ->
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_titleFragment,
                Bundle().apply { putParcelable("trackObject", track) }
            )
        }
        trackAdapter?.onLongItemClick = { track ->
            showDeleteTrackDialog(track.trackId)
            true
        }

        binding.tracksSheetContent.tracksRecyclerView.adapter = trackAdapter
    }

    private fun setupBottomSheet() {
        BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                _binding?.overlay?.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.overlay?.alpha = slideOffset
            }
        })
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
        viewModel.playlistDeleted.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun render(state: PlaylistDetailsUiState) {
        val playlist = state.playlist ?: return

        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.playlistDescription.isVisible = !playlist.description.isNullOrEmpty()

        val durationText = resources.getQuantityString(
            R.plurals.minutes_plurals,
            state.totalDurationMinutes,
            state.totalDurationMinutes
        )
        val trackCountText = resources.getQuantityString(
            R.plurals.tracks_plurals,
            playlist.trackCount,
            playlist.trackCount
        )

        binding.playlistDuration.text = durationText
        binding.playlistTrackCount.text = trackCountText

        Glide.with(this)
            .load(playlist.coverImagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.coverImage)

        trackAdapter?.tracks = state.tracks.toMutableList()
        trackAdapter?.notifyDataSetChanged()

        binding.tracksSheetContent.placeholderEmptyTracks.isVisible = state.tracks.isEmpty()
        binding.tracksSheetContent.tracksRecyclerView.isVisible = state.tracks.isNotEmpty()

        updateMenuBottomSheet(playlist, trackCountText)
    }

    private fun updateMenuBottomSheet(playlist: Playlist, trackCountText: String) {

        binding.menuSheetContent.menuPlaylistName.text = playlist.name
        binding.menuSheetContent.menuTrackCount.text = trackCountText
        Glide.with(this)
            .load(playlist.coverImagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.menuSheetContent.menuCoverImage)
    }

    private fun sharePlaylist() {
        val state = viewModel.uiState.value ?: return
        if (state.tracks.isEmpty()) {
            Snackbar.make(
                binding.root,
                getString(R.string.no_track_sharing),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val shareText = buildShareText(state)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.sgaring_track)))
    }

    private fun buildShareText(state: PlaylistDetailsUiState): String {
        val sb = StringBuilder()
        state.playlist?.let {
            sb.append(it.name).append("\n")
            if (!it.description.isNullOrEmpty()) sb.append(it.description).append("\n")
            sb.append(
                resources.getQuantityString(
                    R.plurals.tracks_plurals,
                    it.trackCount,
                    it.trackCount
                )
            ).append("\n\n")
        }
        state.tracks.forEachIndexed { index, track ->
            val trackDuration =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            sb.append("${index + 1}. ${track.artistName} - ${track.trackName} ($trackDuration)\n")
        }
        return sb.toString()
    }

    private fun showDeleteTrackDialog(trackId: Int) {
        MaterialAlertDialogBuilder(requireContext(), R.style.TextTrackView)
            .setTitle(R.string.delete_track_title)
            .setMessage(R.string.delete_this_track_from_the_playlist)
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.deleteTrack(trackId) }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val playlistName = viewModel.uiState.value?.playlist?.name ?: ""
        MaterialAlertDialogBuilder(requireContext(), R.style.TextTrackView)
            .setTitle(R.string.delete_playlist_title)
            .setMessage(getString(R.string.delete_playlist_message, playlistName))
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.deletePlaylist() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        trackAdapter = null
        _binding = null
    }

    companion object {
        const val PLAYLIST_ID = "playlist_id"
    }
}