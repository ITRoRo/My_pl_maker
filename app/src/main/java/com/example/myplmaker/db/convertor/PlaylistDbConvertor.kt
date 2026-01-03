package com.example.myplmaker.db.convertor

import com.example.myplmaker.db.entity.PlaylistEntity
import com.example.myplmaker.playlist.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(private val gson: Gson) {
    fun map(playlist: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Int>>() {}.type
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIds = gson.fromJson(playlist.trackIds, type),
            trackCount = playlist.trackCount
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIds = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount
        )
    }
}