package com.example.myplmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myplmaker.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId IN (:trackIds)")
    fun getTracksByIds(trackIds: List<Int>): Flow<List<PlaylistTrackEntity>>

    @Query("DELETE FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Int)
}