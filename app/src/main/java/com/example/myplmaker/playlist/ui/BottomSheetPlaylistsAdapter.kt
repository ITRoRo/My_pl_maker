package com.example.myplmaker.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myplmaker.R
import com.example.myplmaker.databinding.PlaylistBottomSheetItemBinding
import com.example.myplmaker.playlist.domain.model.Playlist

class BottomSheetPlaylistsAdapter (
    private val playlists: MutableList<Playlist>,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaylistBottomSheetItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onItemClick(playlist) }
    }

    override fun getItemCount() = playlists.size

    fun updateData(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: PlaylistBottomSheetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: Playlist) {
            binding.playlistNameTextView.text = playlist.name
            val trackCountText = itemView.resources.getQuantityString(
                R.plurals.tracks_plurals, playlist.trackCount, playlist.trackCount
            )
            binding.trackCountTextView.text = trackCountText

            Glide.with(itemView.context)
                .load(playlist.coverImagePath)
                .placeholder(R.drawable.placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.four_dp))
                )
                .into(binding.coverImageView)
        }
    }
}