package com.example.myplmaker.search.ui.fragment

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
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
    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var searchDebounceRunnable: Runnable

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentSearchBinding
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
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        trackAdapter = TrackAdapter(arrayListOf())
        historyAdapter = TrackAdapter(arrayListOf())

        binding.recicleView.adapter = trackAdapter

        showStatus.titleError = binding.titleError
        showStatus.imageError = binding.imageError
        showStatus.textError = binding.textError
        showStatus.textErrorInternet = binding.textErrorInternet
        showStatus.updateButton = binding.updateButton
        showStatus.historyBlock = binding.history
        showStatus.historyText = binding.historyIcon
        showStatus.historyButton = binding.clearHistory
        showStatus.reciclerViewHistoryTrack = binding.viewHistory

        viewModel.load()
    }

    private fun setupObservers() {
        viewModel.getResult().observe(viewLifecycleOwner) { result ->
            when (result.code) {
                null -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recicleView.visibility = View.VISIBLE
                    result.trackList?.let {
                        if (it.isNotEmpty()) {
                            updateTrackList(it)
                            showStatus.showStatus(Konst.ZAG)
                        } else {
                            showStatus.showStatus(Konst.NO_TRACK)
                        }
                    }
                }

                -1 -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recicleView.visibility = View.GONE
                    showStatus.showStatus(Konst.NO_INTERNET)
                }

                -2 -> {
                    binding.progressBar.visibility = View.GONE
                    updateHistoryList(result.historyList)
                    if (result.historyList.isNotEmpty() && binding.inputText.hasFocus()) {
                        showStatus.showStatus(Konst.HISTORY)
                        binding.recicleView.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun updateTrackList(tracks: List<Track>) {
        trackAdapter = TrackAdapter(ArrayList(tracks))
        binding.recicleView.adapter = trackAdapter
        setupTrackClickListener()
    }

    private fun updateHistoryList(tracks: List<Track>) {
        historyAdapter = TrackAdapter(ArrayList(tracks))
        showStatus.reciclerViewHistoryTrack.adapter = historyAdapter
        setupHistoryClickListener()
    }

    private fun setupTrackClickListener() {
        trackAdapter.onItemClick = { trackItem ->
            if (clickDebounce()) {
                binding.inputText.clearFocus()
                viewModel.save(trackItem)
                val bundle = Bundle().apply {
                    putParcelable("trackObject", trackItem)
                }
                findNavController().navigate(
                    R.id.action_searchFragment_to_titleFragment,
                    bundle
                )
            }
        }
    }

    private fun setupHistoryClickListener() {
        historyAdapter.onItemClick = { trackItem ->
            val bundle = Bundle().apply {
                putParcelable("trackObject", trackItem)
            }
            findNavController().navigate(R.id.action_searchFragment_to_titleFragment, bundle)
        }
        }






    private fun setupListeners() {
     //   binding.back.setOnClickListener {
      //      this.finish()
      //  }

        binding.inputText.addTextChangedListener(createTextWatcher())

        binding.clear.setOnClickListener {
            clearSearchAndHideKeyboard()
        }

        binding.inputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchDebounce()
            }
            false
        }

        binding.inputText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.load()
            }
        }

        showStatus.updateButton.setOnClickListener {
            searchDebounce()
        }

        showStatus.historyButton.setOnClickListener {
            viewModel.clearHistory()
            showStatus.showStatus(Konst.ZAG)
        }

        searchDebounceRunnable = Runnable {
            val searchText = binding.inputText.text.toString()
            if (searchText.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                binding.recicleView.visibility = View.GONE
                showStatus.titleError.isVisible = false
                viewModel.search(searchText)
            }
        }
        binding.inputText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputText.text.toString().isEmpty()) {
                viewModel.load()
            }
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()

                viewModel.searchText = text

                if (text.isNotEmpty()) {
                    binding.clear.visibility = View.VISIBLE
                    showStatus.showStatus(Konst.ZAG)
                    searchDebounce()
                } else {
                    binding.clear.visibility = View.GONE
                    binding.recicleView.visibility = View.GONE
                    viewModel.load()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
    }

    private fun clearSearchAndHideKeyboard() {
        binding.inputText.setText("")
        binding.recicleView.visibility = View.GONE
        viewModel.load()
        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.clear.windowToken, 0)
        binding.inputText.clearFocus()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchDebounceRunnable)
        handler.postDelayed(searchDebounceRunnable, CLICK_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

//    override fun onSaveInstanceState(outState: Bundle) {
 //       super.onSaveInstanceState(outState)
  //      outState.putString(PRODUCT_AMOUNT, viewModel.searchText)
  //  }


}