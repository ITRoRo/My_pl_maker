package com.example.myplmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var titleError: LinearLayout
    private lateinit var imageError: ImageView
    private lateinit var textError: TextView
    private lateinit var textErrorInternet: TextView
    private lateinit var updateButton: MaterialButton



    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val clearButton = findViewById<ImageView>(R.id.clear)
        val inputEditText = findViewById<EditText>(R.id.input_text)
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val trackListAd = ArrayList<Track>()
        val trackAdapter = TrackAdapter(trackListAd)
        val searchAdapter = findViewById<RecyclerView>(R.id.recicleView)

        searchAdapter.layoutManager = LinearLayoutManager(this)
        searchAdapter.adapter = trackAdapter

        titleError = findViewById(R.id.title_error)
        imageError = findViewById(R.id.image_error)
        textError = findViewById(R.id.text_error)
        textErrorInternet = findViewById(R.id.text_error_internet)
        updateButton = findViewById(R.id.update_button)




        fun showStatus(konst: Konst) {
            when (konst) {
                Konst.NO_TRACK -> {
                    titleError.isVisible = true
                    textErrorInternet.isVisible = false
                    updateButton.isVisible = false
                    imageError.setImageResource(R.drawable.no_track)
                    textError.setText(R.string.no_track)
                }

                Konst.NO_INTERNET -> {
                    titleError.isVisible = true
                    textErrorInternet.isVisible = true
                    updateButton.isVisible = true
                    imageError.setImageResource(R.drawable.light_mode)
                    textError.setText(R.string.no_internet)
                    textErrorInternet.setText(R.string.no_loading)
                    updateButton.setText(R.string.update)
                }
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
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)




        @SuppressLint("SuspiciousIndentation")
        fun searchis1() {
            trackListAd.clear()
            titleError.isVisible = false
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
                                showStatus(Konst.NO_TRACK)
                            }
                        } else {
                            showStatus(Konst.NO_INTERNET)
                        }
                    }


                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showStatus(Konst.NO_INTERNET)
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
        updateButton.setOnClickListener {
            searchis1()
        }
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {

            View.VISIBLE
        }
    }



    private var textSearch: String = AMOUNT_DEF

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PRODUCT_AMOUNT, textSearch)
    }

    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        const val AMOUNT_DEF = ""
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textSearch = savedInstanceState.getString(PRODUCT_AMOUNT, AMOUNT_DEF)
    }
}