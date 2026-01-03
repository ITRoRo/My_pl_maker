package com.example.myplmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myplmaker.db.dao.TrackDao
import com.example.myplmaker.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): TrackDao
}