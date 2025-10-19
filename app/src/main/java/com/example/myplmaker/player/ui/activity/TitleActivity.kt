package com.example.myplmaker.player.ui.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myplmaker.R
import com.example.myplmaker.creator.DateFormatUtils
import com.example.myplmaker.databinding.ActivityTitleTreckBinding
import com.example.myplmaker.player.ui.PlayerState
import com.example.myplmaker.player.ui.TrackUiState
import com.example.myplmaker.player.ui.view.TitleViewModel
import com.example.myplmaker.search.domain.model.Track

class TitleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTitleTreckBinding
    private lateinit var viewModel: TitleViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTitleTreckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            TitleViewModel.getViewModelFactory(this)
        )[TitleViewModel::class.java]

        val trackItem = intent.getParcelableExtra("trackObject", Track::class.java)
        if (trackItem == null) {
            finish()
            return
        }

        setupUI(trackItem)
        viewModel.initTrack(trackItem)
        observeViewModel()
    }

    private fun setupUI(track: Track) {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.titleTrack.text = track.trackName
        binding.artistName.text = track.artistName
        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country
        binding.timer.text = "00:00"
        binding.time.text = formatTrackTime(track.trackTimeMillis)

        setupAlbumInfo(track)
        setupReleaseDate(track)
        setupAlbumImage(track)
        binding.buttonPlay.setOnClickListener {
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

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            updatePlayerUI(state)
        }
    }

    private fun updatePlayerUI(state: TrackUiState) {
        binding.timer.text = state.currentPosition

        when (state.playerState) {
            PlayerState.DEFAULT -> {
                binding.buttonPlay.isEnabled = false
            }

            PlayerState.PREPARED -> {
                binding.buttonPlay.isEnabled = true
                binding.buttonPlay.setImageResource(R.drawable.play)
            }

            PlayerState.PLAYING -> {
                binding.buttonPlay.setImageResource(R.drawable.stop)
            }

            PlayerState.PAUSED -> {
                binding.buttonPlay.setImageResource(R.drawable.play)
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

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }


}