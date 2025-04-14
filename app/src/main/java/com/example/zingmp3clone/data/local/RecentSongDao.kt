package com.example.zingmp3clone.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zingmp3clone.data.model.RecentSong

@Dao
interface RecentSongDao {
    @Query("SELECT * FROM recent_song ORDER BY playedAt DESC")
    fun getAllRecentSongs(): List<RecentSong>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSong(song: RecentSong)

    @Query("DELETE FROM recent_song WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM recent_song")
    suspend fun deleteAll()
}