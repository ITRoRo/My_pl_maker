package com.example.myplmaker.search.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.myplmaker.R

import com.example.myplmaker.databinding.ActivitySearchBinding
import com.example.myplmaker.player.ui.activity.TitleActivity
import com.example.myplmaker.search.domain.model.Track
import com.example.myplmaker.search.ui.ShowStatus
import com.example.myplmaker.search.ui.TrackAdapter
import com.example.myplmaker.search.ui.model.Konst
import com.example.myplmaker.search.ui.view.SearchViewModel


class SearchActivity : AppCompatActivity() {
    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        const val AMOUNT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var searchDebounceRunnable: Runnable

    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val showStatus = ShowStatus()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory(
                getSharedPreferences(
                    "app_preferences",
                    MODE_PRIVATE
                ), this
            )
        )[SearchViewModel::class.java]

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        trackAdapter = TrackAdapter(arrayListOf())
        historyAdapter = TrackAdapter(arrayListOf())

        binding.recicleView.adapter = trackAdapter

        showStatus.titleError = findViewById(R.id.title_error)
        showStatus.imageError = findViewById(R.id.image_error)
        showStatus.textError = findViewById(R.id.text_error)
        showStatus.textErrorInternet = findViewById(R.id.text_error_internet)
        showStatus.updateButton = findViewById(R.id.update_button)
        showStatus.historyBlock = findViewById(R.id.history)
        showStatus.historyText = findViewById(R.id.history_icon)
        showStatus.historyButton = findViewById(R.id.clear_history)
        showStatus.reciclerViewHistoryTrack = findViewById(R.id.view_history)

        viewModel.load()
    }

    private fun setupObservers() {
        viewModel.getResult().observe(this) { result ->
            when (result.code) {
                null -> { // Успешный поиск треков
                    binding.progressBar.visibility = View.GONE
                    result.trackList?.let {
                        if (it.isNotEmpty()) {
                            updateTrackList(it)
                            showStatus.showStatus(Konst.ZAG)
                        } else {
                            showStatus.showStatus(Konst.NO_TRACK)
                        }
                    }
                }

                -1 -> { // Ошибка сети
                    binding.progressBar.visibility = View.GONE
                    showStatus.showStatus(Konst.NO_INTERNET)
                }

                -2 -> { // Загрузка истории
                    updateHistoryList(result.historyList)
                    if (result.historyList.isNotEmpty() && binding.inputText.hasFocus()) {
                        showStatus.showStatus(Konst.HISTORY)
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
                startActivity(Intent(this, TitleActivity::class.java).apply {
                    putExtra("trackObject", trackItem)
                })
            }
        }
    }

    private fun setupHistoryClickListener() {
        historyAdapter.onItemClick = { trackItem ->
            startActivity(Intent(this, TitleActivity::class.java).apply {
                putExtra("trackObject", trackItem)
            })
        }
    }

    private fun setupListeners() {
        binding.back.setOnClickListener {
            this.finish()
        }

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
        }

        searchDebounceRunnable = Runnable {
            val searchText = binding.inputText.text.toString()
            if (searchText.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                showStatus.titleError.isVisible = false
                viewModel.search(searchText)
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

                    if (binding.inputText.hasFocus()) {
                        viewModel.load()
                    } else {
                        showStatus.showStatus(Konst.ZAG)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
    }

    private fun clearSearchAndHideKeyboard() {
        binding.inputText.setText("")
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PRODUCT_AMOUNT, viewModel.searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.searchText = savedInstanceState.getString(PRODUCT_AMOUNT, AMOUNT_DEF)
        binding.inputText.setText(viewModel.searchText)
    }
}