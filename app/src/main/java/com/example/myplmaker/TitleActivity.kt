package com.example.myplmaker


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
                playTime.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, CLICK_DEBOUNCE_DELAY)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title_treck)


        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            this.finish()
        }


        trackItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("trackObject", Track::class.java)
        } else {
            intent.getParcelableExtra("trackObject") as? Track
        } ?: return
        runTreck()
    }


    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun runTreck() {
        trackItem.let {
            findViewById<TextView>(R.id.title_track).text = it.trackName
            findViewById<TextView>(R.id.artist_name).text = it.artistName
            findViewById<TextView>(R.id.genre).text = it.primaryGenreName
            findViewById<TextView>(R.id.country).text = it.country
            findViewById<TextView>(R.id.timer).text = it.trackTimeMillis.let { it1 ->
                formatTrackTime(
                    it1
                )
            }
            findViewById<TextView>(R.id.time).text = it.trackTimeMillis.let { it1 ->
                formatTrackTime(
                    it1
                )
            }
        }

        playButton = findViewById(R.id.button_play)
        playTime = findViewById(R.id.timer)


        val album = findViewById<TextView>(R.id.album)
        trackItem.collectionName?.let {
            album.text = it
            album.isVisible = true
        } ?: run {
            album.isVisible = false
        }


        val releaseData = findViewById<TextView>(R.id.year)
        trackItem.releaseDate?.let {
            val years = formatData(it)
            releaseData.text = years
            releaseData.isVisible = true
        } ?: run {
            releaseData.isVisible = false
        }


        val bigPoster = findViewById<ImageView>(R.id.trackAva)
        val bigPoster100 = trackItem.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        val context = this
        val cornerRadiusPx = dpToPx(8, context)
        Glide.with(this)
            .load(bigPoster100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusPx))
            .into(bigPoster)

        preparePlayer()
        playTime.setOnClickListener {
            playbackControl()
        }

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("trackObject", trackItem)
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


    private fun preparePlayer() {

        mediaPlayer.setDataSource(trackItem.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.stop)
            playerState = STATE_PREPARED
            playTime.text = START_TIME
        }
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
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(statusTime)
        mediaPlayer.release()
    }

}







