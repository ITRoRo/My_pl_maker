package com.example.myplmaker

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        const val AMOUNT_DEF = ""
        const val MAX_TRACKS = 10
    }

    private lateinit var searchHistory: SearchHistory
    val trackListAd = ArrayList<Track>()
    val showStatus = ShowStatus()
    var historyTracks = ArrayList<Track>(MAX_TRACKS)
    private val searchClass = SearchHistory()


    @SuppressLint("MissingInflatedId")
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ITunesApi::class.java)


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

      // searchAdapter.layoutManager = LinearLayoutManager(this)
        searchAdapter.adapter = trackAdapter
        showStatus.reciclerViewHistoryTrack = findViewById(R.id.view_history)


        searchClass.fromJson(historyTracks)


        trackAdapter.onItemClick = { trackItem ->
            inputEditText.clearFocus()
            searchClass.addTrackInHistory(historyTracks, trackItem)
            historyAdapter.notifyDataSetChanged()

        }
        historyAdapter.onItemClick = { trackItem ->
            inputEditText.clearFocus()
            searchClass.addTrackInHistory(historyTracks, trackItem)
            historyAdapter.notifyDataSetChanged()

        }

        showStatus.titleError = findViewById(R.id.title_error)
        showStatus.imageError = findViewById(R.id.image_error)
        showStatus.textError = findViewById(R.id.text_error)
        showStatus.textErrorInternet = findViewById(R.id.text_error_internet)
        showStatus.updateButton = findViewById(R.id.update_button)
        showStatus.historyBlock = findViewById(R.id.history)
        showStatus.historyText = findViewById(R.id.history_icon)
        showStatus.historyButton = findViewById(R.id.clear_history)
        
        inputEditText.setOnFocusChangeListener { _, _ ->
            if (inputEditText.hasFocus() && historyTracks.isNotEmpty()) {
                showStatus.reciclerViewHistoryTrack.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()
                showStatus.showStatus(Konst.HISTORY)

            }
        }


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

                    View.VISIBLE
                    showStatus.showStatus(Konst.ZAG)
                }
                else  {
                    if(inputEditText.hasFocus() && historyTracks.isNotEmpty()) {
                        showStatus.reciclerViewHistoryTrack.adapter = historyAdapter
                        historyAdapter.notifyDataSetChanged()
                        showStatus.showStatus(Konst.HISTORY)
                    }

                    showStatus.showStatus(Konst.ZAG)
                    View.GONE
                }


            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)




        @SuppressLint("SuspiciousIndentation")
        fun searchis1() {
            trackListAd.clear()
            showStatus.titleError.isVisible = false

            itunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {

                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            trackListAd.clear()
                            trackListAd.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()

                            if (trackListAd.isEmpty()) {
                                showStatus.showStatus(Konst.NO_TRACK)
                            }
                        } else {
                            showStatus.showStatus(Konst.NO_INTERNET)
                        }
                    }


                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showStatus.showStatus(Konst.NO_INTERNET)
                    }

                })
        }



        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchis1()
                true
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
            searchis1()

        }
        showStatus.historyButton.setOnClickListener {
            searchClass.clearHistory(historyTracks)
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
}