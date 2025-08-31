package com.example.myplmaker.ui.search

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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.myplmaker.R


import com.example.myplmaker.data.network.SearchHistory
import com.example.myplmaker.domain.models.Track
import com.example.myplmaker.ui.TrackAdapter
import com.example.myplmaker.ui.title.TitleActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myplmaker.Creator
import com.example.myplmaker.data.dto.TrackSearchResponse
import com.example.myplmaker.domain.api.TracksInteractor


class SearchActivity : AppCompatActivity() {
    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        const val AMOUNT_DEF = ""
        const val MAX_TRACKS = 10
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }


    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var progressBar: ProgressBar


    private val trackListAd = ArrayList<Track>()
    private val showStatus = ShowStatus()
    private var historyTracks = ArrayList<Track>(MAX_TRACKS)
    private val searchHistory = SearchHistory()

    private val tracksInteractor = Creator.provideTrackInteractor()



    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val clearButton = findViewById<ImageView>(R.id.clear)
        val inputEditText = findViewById<EditText>(R.id.input_text)
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val trackAdapter = TrackAdapter(trackListAd)
        val searchAdapter = findViewById<RecyclerView>(R.id.recicleView)
        val historyAdapter = TrackAdapter(historyTracks)


        progressBar = findViewById(R.id.progressBar)


        // Установка адаптера для рециклерного представления
        searchAdapter.adapter = trackAdapter
        showStatus.reciclerViewHistoryTrack = findViewById(R.id.view_history)
        searchHistory.fromJson(historyTracks)

        // Обработчик кликов для треков
        if (clickDebounce()) {
            trackAdapter.onItemClick = { trackItem: Track ->
                inputEditText.clearFocus()
                searchHistory.addTrackInHistory(historyTracks, trackItem)
                startActivity(Intent(this, TitleActivity::class.java).apply {
                    putExtra("trackObject", trackItem)
                })
                historyAdapter.notifyDataSetChanged()
            }
        }

        historyAdapter.onItemClick = { trackItem: Track ->
            startActivity(Intent(this, TitleActivity::class.java).apply {
                putExtra("trackObject", trackItem)
            })
        }


        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && historyTracks.isNotEmpty()) {
                trackListAd.clear()
                showStatus.reciclerViewHistoryTrack.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()
                showStatus.showStatus(Konst.HISTORY)
            }
        }


        showStatus.titleError = findViewById(R.id.title_error)
        showStatus.imageError = findViewById(R.id.image_error)
        showStatus.textError = findViewById(R.id.text_error)
        showStatus.textErrorInternet = findViewById(R.id.text_error_internet)
        showStatus.updateButton = findViewById(R.id.update_button)
        showStatus.historyBlock = findViewById(R.id.history)
        showStatus.historyText = findViewById(R.id.history_icon)
        showStatus.historyButton = findViewById(R.id.clear_history)


        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textSearch = s.toString()
                if (!s.isNullOrEmpty()) {
                    clearButton.visibility = View.VISIBLE
                    showStatus.showStatus(Konst.ZAG)

                    searchDebounce()

                } else {
                    if (inputEditText.hasFocus() && historyTracks.isNotEmpty()) {
                        trackListAd.clear()
                        showStatus.reciclerViewHistoryTrack.adapter = historyAdapter
                        historyAdapter.notifyDataSetChanged()
                        showStatus.showStatus(Konst.HISTORY)
                    }
                    showStatus.showStatus(Konst.ZAG)
                    clearButton.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }


        inputEditText.addTextChangedListener(simpleTextWatcher)



        runnable = Runnable {
            if (inputEditText.text.isNotEmpty()) {
                trackListAd.clear()
                showStatus.titleError.isVisible = false
                progressBar.visibility = View.VISIBLE
                tracksInteractor.searchTracks(inputEditText.text.toString(),
                    object : TracksInteractor.TracksConsumer {
                        override fun consumer(foundTracks: List<Track>) {
                            runOnUiThread {
                                if (foundTracks.isNotEmpty()) {
                                    progressBar.visibility = View.GONE
                                    trackListAd.clear()
                                    trackListAd.addAll(foundTracks)
                                    trackAdapter.notifyDataSetChanged()
                                    if (trackListAd.isEmpty()) {
                                        showStatus.showStatus(Konst.NO_TRACK)
                                    }
                                } else {
                                    showStatus.showStatus(Konst.NO_INTERNET)
                                    progressBar.visibility = View.GONE
                                }
                            }
                        }

                            override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                                showStatus.showStatus(Konst.NO_INTERNET)
                                progressBar.visibility = View.GONE
                            }

                    })
            }
        }
/*
        runnable = Runnable {
            if (inputEditText.text.isNotEmpty()) {


                showStatus.showStatus(Konst.ZAG)

                tracksInteractor.searchTracks(
                    inputEditText.text.toString(),
                    object : TracksInteractor.TracksConsumer {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun consume(recievedTracks: List<Track>) {
                            runOnUiThread {
                                if (recievedTracks.isNotEmpty()) {
                                    trackListAd.clear()
                                    trackListAd.addAll(recievedTracks)
                                    showStatus.titleError.isVisible = false
                                    progressBar.visibility = View.VISIBLE
                                    trackAdapter.notifyDataSetChanged()

                                } else if (recievedTracks.isEmpty()) {
                                    showStatus.showStatus(Konst.NO_TRACK)
                                }
                            }
                        }

                        override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                            showStatus.showStatus(Konst.NO_INTERNET)
                            progressBar.visibility = View.GONE
                        }
                        }
                    )
            }
        }
*/

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchDebounce()

            }
            false
        }


        clearButton.setOnClickListener {
            trackListAd.clear()
            trackAdapter.notifyDataSetChanged()
            inputEditText.setText("")
            inputMethodManager.hideSoftInputFromWindow(clearButton.windowToken, 0)
            inputEditText.clearFocus()
        }


        inputEditText.setText(textSearch)
        showStatus.updateButton.setOnClickListener {
            searchDebounce()
        }


        showStatus.historyButton.setOnClickListener {
            searchHistory.clearHistory(historyTracks)
            historyAdapter.notifyDataSetChanged()
            showStatus.showStatus(Konst.ZAG)
        }
    }


    private var textSearch: String = AMOUNT_DEF
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PRODUCT_AMOUNT, textSearch)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textSearch = savedInstanceState.getString(PRODUCT_AMOUNT, AMOUNT_DEF)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, CLICK_DEBOUNCE_DELAY)
    }

}