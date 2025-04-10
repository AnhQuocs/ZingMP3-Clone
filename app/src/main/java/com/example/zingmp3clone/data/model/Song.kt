package com.example.zingmp3clone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song (
    @PrimaryKey val id: Int,
    val name: String,
    val artist: String,
    val duration: Int,
    val thumbnail: Int
)