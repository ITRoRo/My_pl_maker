package com.example.myplmaker.search.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myplmaker.R
import com.example.myplmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale


class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.musicianName)
    private val artwor: ImageView = itemView.findViewById(R.id.trackAva)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val trackId: TextView = itemView.findViewById(R.id.track_id)


    private val placeholder = R.drawable.placeholder

    fun bind(model: Track) {
        trackId.text = model.trackId.toString()
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)

        val trackImage = model.artworkUrl100
        val context = itemView.context
        setImage(context, trackImage, artwor, placeholder, 2.0f)


        itemView.setOnClickListener {
            addTrack(model)
        }
    }

    private fun setImage(
        itemView: Context,
        trackImage: String?,
        artwor: ImageView,
        placeholder: Int,
        dp: Float
    ) {
        Glide.with(itemView)
            .load(trackImage)
            .placeholder(placeholder)
            .transform(RoundedCorners(dpToPx(itemView, dp)))
            .into(artwor)
    }

    private fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun addTrack(model: Track): Track {
        return model
    }
}