package com.example.myplmaker.search.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myplmaker.R

import com.example.myplmaker.search.domain.model.Track

class TrackAdapter(private val trackList: ArrayList<Track>) : RecyclerView.Adapter<TrackHolder>() {

    var onItemClick: ((trackItem: Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val trackItem = trackList[position]
        holder.bind(trackItem)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(trackItem)
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}