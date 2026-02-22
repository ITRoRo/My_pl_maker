package com.example.myplmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myplmaker.db.dao.PlaylistDao
import com.example.myplmaker.db.dao.PlaylistTrackDao
import com.example.myplmaker.db.dao.TrackDao
import com.example.myplmaker.db.entity.PlaylistEntity
import com.example.myplmaker.db.entity.PlaylistTrackEntity
import com.example.myplmaker.db.entity.TrackEntity

@Database(version = 5, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}