package com.example.zingmp3clone.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zingmp3clone.data.model.Song
import com.example.zingmp3clone.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs = _songs.asStateFlow()

    init {
        viewModelScope.launch {
            val allSongs = songRepository.getAllSongs()
            Log.d("SONG_VM", "Fetched ${allSongs.size} songs")
            _songs.value = songRepository.getAllSongs()
        }
    }
}
