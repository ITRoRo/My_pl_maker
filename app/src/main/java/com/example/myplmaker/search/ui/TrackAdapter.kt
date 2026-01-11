package com.example.myplmaker.search.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myplmaker.R

import com.example.myplmaker.search.domain.model.Track

class TrackAdapter() : RecyclerView.Adapter<TrackHolder>() {

    var tracks = mutableListOf<Track>()
    var onItemClick: ((Track) -> Unit)? = null
    var onLongItemClick: ((Track) -> Boolean)? = null

        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val trackItem = tracks[position]
        holder.bind(trackItem)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(trackItem)
        }
        holder.itemView.setOnLongClickListener {
            onLongItemClick?.invoke(trackItem) ?: false
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}