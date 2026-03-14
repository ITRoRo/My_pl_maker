package com.example.myplmaker.player.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myplmaker.R
import com.example.myplmaker.creator.DateFormatUtils
import com.example.myplmaker.databinding.FragmentTitleTreckBinding
import com.example.myplmaker.player.service.MusicService
import com.example.myplmaker.player.ui.PlayerState
import com.example.myplmaker.player.ui.TrackUiState
import com.example.myplmaker.player.ui.view.TitleViewModel
import com.example.myplmaker.playlist.ui.BottomSheetPlaylistsAdapter
import com.example.myplmaker.search.domain.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class TitleFragment : Fragment() {

    private lateinit var binding: FragmentTitleTreckBinding
    private val viewModel: TitleViewModel by viewModel()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var bottomSheetAdapter: BottomSheetPlaylistsAdapter? = null

    private var trackItem: Track? = null
    private var isServiceBound = false


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            viewModel.setAudioPlayerControl(binder.getService())
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeAudioPlayerControl()
            isServiceBound = false
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
        trackItem?.let { bindMusicService(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTitleTreckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            trackItem = arguments?.getParcelable("trackObject", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            trackItem = arguments?.getParcelable("trackObject")
        }

        if (trackItem == null) {
            findNavController().popBackStack()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            trackItem?.let { bindMusicService(it) }
        }

        viewModel.initTrack(trackItem!!)
        initBottomSheet()
        observeViewModel()

        binding.buttonHeart.setOnClickListener {
            viewModel.onFavoriteClicked()
        }


        binding.buttonPlus.setOnClickListener {
            binding.overlay.isVisible = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistBsButton.setOnClickListener {
            findNavController().navigate(R.id.action_titleFragment_to_newPlaylistFragment)
        }


    }


    private fun bindMusicService(track: Track) {
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            putExtra("song_url", track.previewUrl)
            putExtra("track_title", track.trackName)
            putExtra("artist_name", track.artistName)
        }
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection)
            isServiceBound = false
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setupUI(track: Track) {


        binding.titleTrack.text = track.trackName
        binding.artistName.text = track.artistName
        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country
        binding.time.text = formatTrackTime(track.trackTimeMillis)

        setupAlbumInfo(track)
        setupReleaseDate(track)
        setupAlbumImage(track)
        binding.buttonPlay.setOnPlaybackClickListener { isPlayingNow ->
            viewModel.playbackControl()
        }
    }

    private fun setupAlbumInfo(track: Track) {
        binding.album.isVisible = track.collectionName != null
        binding.album.text = track.collectionName
    }

    private fun setupReleaseDate(track: Track) {
        binding.year.isVisible = track.releaseDate != null
        track.releaseDate?.let {
            val formattedDate = DateFormatUtils.formatReleaseDate(it)
            binding.year.text = formattedDate
        }
    }

    private fun setupAlbumImage(track: Track) {
        val bigPosterUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(bigPosterUrl)
            .placeholder(R.drawable.placeholder)
            .into(binding.trackAva)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.buttonHeart.setImageResource(R.drawable.likee_red)
        } else {
            binding.buttonHeart.setImageResource(R.drawable.likee)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updatePlayerUI(state)
            updateFavoriteIcon(state.isFavorite)
            bottomSheetAdapter?.updateData(state.playlists)
            state.track?.let { track ->
                if (binding.titleTrack.text != track.trackName) {
                    setupUI(track)
                }
            }
        }



        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            val successMessageTemplate = getString(R.string.added)
            if (message.startsWith(successMessageTemplate)) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetAdapter = BottomSheetPlaylistsAdapter(mutableListOf()) { playlist ->
            viewModel.onAddTrackToPlaylistClicked(playlist)
        }
        binding.playlistsBsRecyclerView.adapter = bottomSheetAdapter

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = (slideOffset + 1) / 2
                binding.overlay.alpha = alpha
            }
        })

        binding.buttonPlus.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun updatePlayerUI(state: TrackUiState) {
        binding.timer.text = state.currentPosition

        when (state.playerState) {
            PlayerState.DEFAULT -> {
                binding.buttonPlay.isEnabled = false
                binding.buttonPlay.setPlaying(false)
            }

            PlayerState.PREPARED -> {
                binding.buttonPlay.isEnabled = true
                binding.buttonPlay.setPlaying(false)
            }

            PlayerState.PLAYING -> {
                binding.buttonPlay.isEnabled = true
                binding.buttonPlay.setPlaying(true)
            }

            PlayerState.PAUSED -> {
                binding.buttonPlay.isEnabled = true
                binding.buttonPlay.setPlaying(false)
            }
        }
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        return String.format(
            "%02d:%02d",
            (trackTimeMillis / 1000) / 60,
            (trackTimeMillis / 1000) % 60
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}