package com.example.myplmaker

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale


class TrackHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.musicianName)
    private val artwor : ImageView = itemView.findViewById(R.id.trackAva)
    private val trackTime : TextView = itemView.findViewById(R.id.trackTime)
    private val trackId : TextView = itemView.findViewById(R.id.track_id)

    fun bind(model: Track) {
        trackId.text = model.trackId
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault())
            .format(model.trackTimeMillis.toInt())

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(2.0f, itemView)))
            .into(artwor)
        itemView.setOnClickListener {
            addTrack(model)
        }

    }

    private fun dpToPx(dp: Float, context: View): Int {

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