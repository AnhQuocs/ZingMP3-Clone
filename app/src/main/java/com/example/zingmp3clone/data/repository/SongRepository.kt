package com.example.zingmp3clone.data.repository

import com.example.zingmp3clone.data.local.SongDao
import com.example.zingmp3clone.data.model.Song
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val songDao: SongDao
) {
    suspend fun getAllSongs(): List<Song> {
        return songDao.getAllSongs()
    }
}