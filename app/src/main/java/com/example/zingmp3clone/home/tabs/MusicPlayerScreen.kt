package com.example.zingmp3clone.home.tabs

import android.media.MediaPlayer
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.More
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zingmp3clone.ui.theme.ZingMP3CloneTheme
import com.example.zingmp3clone.viewmodel.SongViewModel

@Composable
fun MusicPlayerScreen(modifier: Modifier = Modifier, songId: Int?) {
    val context = LocalContext.current
    val songViewModel: SongViewModel = hiltViewModel()
    val songList by songViewModel.songs.collectAsState(initial = emptyList())

//    val song = remember(songId, songList) {
//        songList.find { it.id == songId }
//    }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) } // tiến trình
    var duration by remember { mutableStateOf(0) } // thời gian

    var currentSongIndex by remember(songList, songId) {
        mutableStateOf(songList.indexOfFirst { it.id == songId })
    }

    val song = remember(currentSongIndex, songList) {
        songList.getOrNull(currentSongIndex)
    }

    var progressThread: Thread? by remember { mutableStateOf(null) }

    fun startUpdatingProgress(player: MediaPlayer) {
        progressThread?.interrupt()
        progressThread = object : Thread() {
            override fun run() {
                try {
                    while (!isInterrupted && player.isPlaying) {
                        Thread.sleep(500)
                        progress = player.currentPosition / player.duration.toFloat()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        progressThread?.start()
    }

    DisposableEffect(song) {
        song ?. let {
            val player = MediaPlayer.create(context, it.id)
            mediaPlayer = player
            player.start()
            isPlaying = true
            duration = player.duration

            // Cập nhật tiến độ liên tục
            val updateProgress = object: Thread() {
                override fun run() {
                    try {
                        while (player.isPlaying) {
                            Thread.sleep(1000)
                            progress = player.currentPosition / player.duration.toFloat()
                        }
                    } catch (e: Exception) {}
                }
            }
            updateProgress.start()
        }

        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        }
    }

    LaunchedEffect(currentSongIndex) {
        mediaPlayer?.release()
        val newSong = songList.getOrNull(currentSongIndex)
        newSong?.let {
            val player = MediaPlayer.create(context, it.id)
            mediaPlayer = player
            player.start()
            isPlaying = true
            duration = player.duration

            val updateProgress = object : Thread() {
                override fun run() {
                    try {
                        while (player.isPlaying) {
                            Thread.sleep(1000)
                            progress = player.currentPosition / player.duration.toFloat()
                        }
                    } catch (e: Exception) {}
                }
            }
            updateProgress.start()
        }
    }


    fun onNext() {
        if (songList.isNotEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % songList.size
        }
    }

    fun onPrevious() {
        if (songList.isNotEmpty()) {
            currentSongIndex = if (currentSongIndex - 1 < 0) songList.size - 1 else currentSongIndex - 1
        }
    }

    song?.let {
        Scaffold(
            topBar = { TopMusicPlayerBar()}
        ) { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)) {
                Text(text = "🎵 Đang phát: ${it.name}", style = MaterialTheme.typography.headlineSmall)
                Text(text = "👤 Ca sĩ: ${it.artist}")

                Spacer(Modifier.height(32.dp))

                MusicControlPanel(
                    currentTime = formatTime((progress * duration).toInt()),
                    totalTime = formatTime(duration),
                    progress = progress,
                    isPlaying = isPlaying,
                    isShuffling = false,
                    isRepeating = false,
                    onSeekChanged = { newProgress ->
                        mediaPlayer?.seekTo((newProgress * duration).toInt())
                        progress = newProgress
                    },
                    onPlayPause = {
                        mediaPlayer?.let { player ->
                            if (player.isPlaying) {
                                player.pause()
                                isPlaying = false
                            } else {
                                player.start()
                                isPlaying = true
                                startUpdatingProgress(player)
                            }
                        }
                    },
                    onNext = {onNext()},
                    onPrevious = {onPrevious()},
                    onShuffleToggle = {},
                    onRepeatToggle = {}
                )
            }
        }
    } ?: Text("Not found!")
}

fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}


@Composable
fun MusicControlPanel(
    currentTime: String,
    totalTime: String,
    progress: Float,
    isPlaying: Boolean,
    isShuffling: Boolean,
    isRepeating: Boolean,
    onSeekChanged: (Float) -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Slider(
            value = progress,
            onValueChange = onSeekChanged,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(currentTime, color = Color.White)
            Text(totalTime, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onShuffleToggle) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffling) Color(0xFF9C27B0) else Color.White
                )
            }

            IconButton(onClick = onPrevious) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White)
            }

            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
            }

            IconButton(onClick = onRepeatToggle) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (isRepeating) Color(0xFF9C27B0) else Color.White
                )
            }
        }
    }
}

@Composable
fun TopMusicPlayerBar(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(30.dp)
        )

        Text(
            "Bài hát của tôi",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Icon(
            Icons.Default.MoreVert,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Preview
@Composable
private fun TopMusicPlayerBarPreview() {
    ZingMP3CloneTheme {
        TopMusicPlayerBar()
    }
}

@Composable
fun SongInfo(modifier: Modifier = Modifier) {

}