package com.example.myplmaker.db.convertor

import com.example.myplmaker.db.entity.PlaylistTrackEntity
import com.example.myplmaker.search.domain.model.Track

class PlaylistTrackDbConvertor {
    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            System.currentTimeMillis()
        )
    }

    fun map(trackEntity: PlaylistTrackEntity): Track {
        return Track(
            trackName = trackEntity.trackName ?: "",
            artistName = trackEntity.artistName ?: "",
            trackTimeMillis = trackEntity.trackTimeMillis,
            artworkUrl100 = trackEntity.artworkUrl100 ?: "",
            trackId = trackEntity.trackId,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseDate,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl
        )
    }
}