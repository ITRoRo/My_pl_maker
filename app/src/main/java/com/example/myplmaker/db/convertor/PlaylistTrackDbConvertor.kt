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


}