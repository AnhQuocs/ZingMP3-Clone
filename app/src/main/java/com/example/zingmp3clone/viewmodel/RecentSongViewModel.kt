package com.example.zingmp3clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zingmp3clone.data.model.RecentSong
import com.example.zingmp3clone.data.repository.RecentSongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentSongViewModel @Inject constructor(
    private val recentSongRepository: RecentSongRepository
) : ViewModel() {

    private val _recentSong = MutableStateFlow<List<RecentSong>>(emptyList())
    val recentSong = _recentSong.asStateFlow()

    private suspend fun getAllRecentSongs() {
        _recentSong.value = recentSongRepository.getAllRecentSongs()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllRecentSongs()
        }
    }

    fun insertRecentSongById(songId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val song = recentSongRepository.getSongById(songId)
            song?.let {
                val recent = RecentSong(
                    id = it.id,
                    name = it.name,
                    artist = it.artist,
                    duration = it.duration,
                    thumbnail = it.thumbnail,
                    playedAt = System.currentTimeMillis()
                )
                recentSongRepository.insertRecentSong(recent)
                _recentSong.value = recentSongRepository.getAllRecentSongs()
            }
        }
    }
}
