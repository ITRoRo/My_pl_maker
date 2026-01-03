package com.example.myplmaker.playlist.fragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myplmaker.R
import com.example.myplmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.myplmaker.playlist.ui.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()

    private lateinit var confirmDialog: MaterialAlertDialogBuilder


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.setCoverUri(uri)
                binding.coverImage.setImageURI(uri)
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

        setupDialog()
        setupListeners()
        setupViewModelObservers()
        setupBackPressed()
    }

    private fun setupDialog() {
        confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.TextTrackView)
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { _, _ -> }
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().navigateUp()
            }
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
            val filePath = saveCoverToInternalStorage(viewModel.getCoverUri())
            viewModel.createPlaylist(filePath)
        }


    }

    private fun setupViewModelObservers() {
        viewModel.isButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.createButton.isEnabled = isEnabled
        }

        viewModel.finishScreen.observe(viewLifecycleOwner) { playlistName ->
            Toast.makeText(requireContext(), "Плейлист $playlistName создан", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        viewModel.showConfirmDialog.observe(viewLifecycleOwner) {
            confirmDialog.show()
        }

        viewModel.closeScreen.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun setupBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackClicked()
                }
            })
    }


    private fun saveCoverToInternalStorage(uri: Uri?): String? {
        if (uri == null) return null

        val directory = File(requireContext().filesDir, "playlist_covers")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val fileName = "cover_${System.currentTimeMillis()}.jpg"
        val file = File(directory, fileName)

        try {
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return file.absolutePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}