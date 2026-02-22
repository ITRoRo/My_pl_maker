package com.example.myplmaker.search.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myplmaker.R
import com.example.myplmaker.databinding.FragmentSearchBinding
import com.example.myplmaker.search.domain.model.Track
import com.example.myplmaker.search.ui.ShowStatus
import com.example.myplmaker.search.ui.TrackAdapter
import com.example.myplmaker.search.ui.model.Konst
import com.example.myplmaker.search.ui.view.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val showStatus = ShowStatus()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupAdapters()
        setupObservers()
        setupListeners()
        viewModel.load()
    }


    private fun setupUI() {

        showStatus.titleError = binding.titleError
        showStatus.imageError = binding.imageError
        showStatus.textError = binding.textError
        showStatus.textErrorInternet = binding.textErrorInternet
        showStatus.updateButton = binding.updateButton
        showStatus.historyBlock = binding.history
        showStatus.historyText = binding.historyIcon
        showStatus.historyButton = binding.clearHistory
        showStatus.reciclerViewHistoryTrack = binding.viewHistory

    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter()
        historyAdapter = TrackAdapter()
        binding.recicleView.adapter = trackAdapter
        binding.viewHistory.adapter = historyAdapter

        trackAdapter.onItemClick = { track ->
            if (viewModel.clickDebounce()) {
                viewModel.save(track)
                navigateToPlayer(track)
            }
        }

        historyAdapter.onItemClick = { track ->
            if (viewModel.clickDebounce()) {
                navigateToPlayer(track)
            }
        }
    }

    private fun navigateToPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_titleFragment,
            Bundle().apply { putParcelable("trackObject", track) }
        )
    }

    private fun setupObservers() {

        viewModel.observeState().observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            when (result.code) {
                200 -> {
                    if (result.trackList.isNullOrEmpty()) {
                        showStatus.showStatus(Konst.NO_TRACK)
                    } else {
                        showStatus.showStatus(Konst.ZAG)
                        binding.recicleView.visibility = View.VISIBLE
                        updateTrackList(result.trackList)
                    }
                }

                -1 -> {
                    showStatus.showStatus(Konst.NO_INTERNET)
                }

                -2 -> {
                    updateHistoryList(result.historyList)
                    if (result.historyList.isNotEmpty() && binding.inputText.text.isEmpty()) {
                        showStatus.showStatus(Konst.HISTORY)
                    } else {
                        showStatus.showStatus(Konst.ZAG)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.clear.setOnClickListener {
            binding.inputText.setText("")

        }

        binding.inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                viewModel.searchDebounce(s.toString())

                if (!s.isNullOrEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recicleView.visibility = View.GONE
                    showStatus.showStatus(Konst.ZAG)
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.recicleView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.inputText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputText.text.isEmpty()) {
                viewModel.load()
            }
        }

        showStatus.updateButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.recicleView.visibility = View.GONE
            showStatus.showStatus(Konst.ZAG)
            viewModel.searchDebounce(binding.inputText.text.toString())
        }

        showStatus.historyButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun updateTrackList(tracks: List<Track>) {
        trackAdapter.tracks = tracks.toMutableList()
    }

    private fun updateHistoryList(tracks: List<Track>) {
        historyAdapter.tracks = tracks.toMutableList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




