package com.example.myplmaker.playlist.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myplmaker.R
import com.example.myplmaker.databinding.FragmentNewPlaylistBinding
import com.example.myplmaker.playlist.domain.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.myplmaker.playlist.ui.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.getCoverUri(uri)
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.eight_dp)))
                    .into(binding.coverImage)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getInt(PLAYLIST_ID_KEY) ?: -1
        viewModel.init(playlistId)

        if (playlistId != -1) {
            binding.toolbarNewPlaylist.title = getString(R.string.edit2)
            binding.createButton.text = getString(R.string.save)
        }

        setupListeners()
        setupViewModelObservers()
        setupBackPressed()
    }


    private fun setupListeners() {

        binding.toolbarNewPlaylist.setNavigationOnClickListener {
            viewModel.onBackClicked()
        }

        binding.coverImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChanged(text.toString())
        }

        binding.descriptionEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChanged(text.toString())
        }

        binding.createButton.setOnClickListener {
            viewModel.onSaveButtonClicked()
        }
    }

    private fun setupViewModelObservers() {
        viewModel.isButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.createButton.isEnabled = isEnabled
        }

        viewModel.finishScreen.observe(viewLifecycleOwner) { playlistName ->
            val message = getString(R.string.playlist_created2, playlistName)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        viewModel.showConfirmDialog.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext(), R.style.TextTrackView)
                .setTitle(R.string.finish_creating_playlist)
                .setMessage(R.string.al_unsaved_data)
                .setNegativeButton(R.string.cancel2) { _, _ -> }
                .setPositiveButton(R.string.finish) { _, _ ->
                    findNavController().navigateUp()
                }
                .show()
        }

        viewModel.playlistDataToRender.observe(viewLifecycleOwner) { playlist ->
            fillData(playlist)
        }

        viewModel.closeScreen.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun setupBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackClicked()
                }
            })
    }

    private fun fillData(playlist: Playlist) {
        binding.nameEditText.setText(playlist.name)
        binding.descriptionEditText.setText(playlist.description)

        playlist.coverImagePath?.let { path ->
            val uri = Uri.parse(path)
            viewModel.getCoverUri(uri)
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.group_180)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelSize(R.dimen.grid_corner_radius))
                )
                .into(binding.coverImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PLAYLIST_ID_KEY = "playlist_id_key"
    }
}