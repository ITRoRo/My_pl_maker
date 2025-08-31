package com.example.myplmaker.ui.title


import android.annotation.SuppressLint
import android.app.usage.NetworkStats.Bucket.STATE_DEFAULT
import android.bluetooth.BluetoothA2dp.STATE_PLAYING
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myplmaker.R
import com.example.myplmaker.domain.models.Track
import java.io.IOException
//import com.example.myplmaker.TitleActivity.Companion.DELAY

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


@Suppress("DEPRECATION")
class TitleActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val START_TIME = "00:00"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var trackItem: Track
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var playerState = STATE_DEFAULT

    private lateinit var playButton: ImageView
    private lateinit var playTime: TextView

    private val statusTime = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                playTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, CLICK_DEBOUNCE_DELAY)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title_treck)

        // Инициализация кнопки "Назад"
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        trackItem = intent.getParcelableExtra("trackObject", Track::class.java) ?: return
        initializeUI()
        preparePlayer()
    }

    private fun initializeUI() {
        // Установка текстов
        findViewById<TextView>(R.id.title_track).text = trackItem.trackName
        findViewById<TextView>(R.id.artist_name).text = trackItem.artistName
        findViewById<TextView>(R.id.genre).text = trackItem.primaryGenreName
        findViewById<TextView>(R.id.country).text = trackItem.country
        findViewById<TextView>(R.id.timer).text = formatTrackTime(trackItem.trackTimeMillis)
        findViewById<TextView>(R.id.time).text = formatTrackTime(trackItem.trackTimeMillis)

        playButton = findViewById(R.id.button_play)
        playButton.setOnClickListener {
            playbackControl()
        }
        playTime = findViewById(R.id.timer)
        // Установка информации об альбоме и дате
        setupAlbumInfo()
        setupReleaseDate()

        // Загрузка изображения альбома
        setupAlbumImage()
    }

    private fun setupAlbumInfo() {
        val albumTextView = findViewById<TextView>(R.id.album)
        albumTextView.isVisible = trackItem.collectionName != null
        albumTextView.text = trackItem.collectionName
    }

    private fun setupReleaseDate() {
        val releaseData = findViewById<TextView>(R.id.year)
        releaseData.isVisible = trackItem.releaseDate != null
        trackItem.releaseDate?.let {
            val years = formatData(it)
            releaseData.text = years
        }
    }

    private fun setupAlbumImage() {
        val bigPoster = findViewById<ImageView>(R.id.trackAva)
        val bigPosterUrl = trackItem.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(bigPosterUrl)
            .placeholder(R.drawable.placeholder)
            .into(bigPoster)
    }
    private fun formatData(dateString: String): String? {
        return try {
            val originalData =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
            val date = originalData.parse(dateString)
            date?.let {
                val originalYear = SimpleDateFormat("yyyy", Locale.getDefault())
                originalYear.format(it)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun preparePlayer() {
        try {
            mediaPlayer.setDataSource(trackItem.previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playButton.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                resetPlayer()
            }
        } catch (e: IOException) {
            e.printStackTrace() // Обработка ошибок
        }
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }


    @SuppressLint("DefaultLocale")
    private fun formatTrackTime(trackTimeMillis: Long): String {
        return String.format(
            "%02d:%02d",
            (trackTimeMillis / 1000) / 60,
            (trackTimeMillis / 1000) % 60
        )
    }

    private fun resetPlayer() {
        playButton.setImageResource(R.drawable.stop)
        playerState = STATE_PREPARED
        playTime.text = START_TIME
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.stop)
        handler.post(statusTime)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(statusTime)
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(statusTime)
        mediaPlayer.release()
    }
}







