package com.example.zingmp3clone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_song")
data class RecentSong (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val artist: String,
    val duration: Int,
    val thumbnail: Int,
    val playedAt: Long = System.currentTimeMillis()
)