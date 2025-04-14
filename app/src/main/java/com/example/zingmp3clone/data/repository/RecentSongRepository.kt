package com.example.zingmp3clone.data.repository

import com.example.zingmp3clone.data.local.RecentSongDao
import com.example.zingmp3clone.data.local.SongDao
import com.example.zingmp3clone.data.model.RecentSong
import com.example.zingmp3clone.data.model.Song
import javax.inject.Inject

class RecentSongRepository @Inject constructor(
    private val songDao: SongDao,
    private val recentSongDao: RecentSongDao
) {
    suspend fun getSongById(id: Int): Song? = songDao.getSong(id)

    suspend fun insertRecentSong(song: RecentSong) = recentSongDao.insertRecentSong(song)

    suspend fun getAllRecentSongs(): List<RecentSong> = recentSongDao.getAllRecentSongs()

    suspend fun deleteById(id: Int) { recentSongDao.deleteById(id) }
}