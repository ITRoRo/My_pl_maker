package com.example.myplmaker


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Suppress("DEPRECATION")
class TitleActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)


    private lateinit var trackItem: Track


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

    private fun formatTrackTime(trackTimeMillis: Long): String {
        return String.format(
            "%02d:%02d",
            (trackTimeMillis / 1000) / 60,
            (trackTimeMillis / 1000) % 60
        )
    }


}
