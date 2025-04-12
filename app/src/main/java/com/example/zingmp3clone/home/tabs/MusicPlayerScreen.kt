package com.example.zingmp3clone.home.tabs

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import com.example.zingmp3clone.R
import com.example.zingmp3clone.ui.theme.ZingMP3CloneTheme
import com.example.zingmp3clone.viewmodel.RecentSongViewModel
import com.example.zingmp3clone.viewmodel.SongViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import android.graphics.Color as AndroidColor

@Composable
fun MusicPlayerScreen(
    modifier: Modifier = Modifier,
    songId: Int?,
    onBackHome: () -> Unit,
    recentSongViewModel: RecentSongViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val songViewModel: SongViewModel = hiltViewModel()
    val songList by songViewModel.songs.collectAsState(initial = emptyList())

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isAnimation by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(0) }
    var progressThread: Thread? by remember { mutableStateOf(null) }

    var currentSongIndex by remember(songList, songId) {
        mutableStateOf(songList.indexOfFirst { it.id == songId })
    }

    val song = remember(currentSongIndex, songList) {
        songList.getOrNull(currentSongIndex)
    }

    var dominantColor by remember { mutableStateOf(Color(0xFF3d1e37)) }

    val systemUiController = rememberSystemUiController()

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

    fun playSongAtIndex(index: Int) {
        mediaPlayer?.release()
        mediaPlayer = null


        val newSong = songList.getOrNull(index) ?: return
        val player = MediaPlayer.create(context, newSong.id)

        recentSongViewModel.insertRecentSongById(newSong.id)

        mediaPlayer = player
        currentSongIndex = index
        isPlaying = true
        isAnimation = true
        duration = player.duration
        progress = 0f

        player.start()
        startUpdatingProgress(player)

        player.setOnCompletionListener {
            if (currentSongIndex < songList.lastIndex) {
                playSongAtIndex(currentSongIndex + 1)
            } else {
                // Nếu muốn phát lại từ đầu: playSongAtIndex(0)
                isPlaying = false
                isAnimation = false
                progress = 1f
            }
        }
    }

    LaunchedEffect(songList, songId) {
        if (songList.isNotEmpty() && currentSongIndex in songList.indices) {
            playSongAtIndex(currentSongIndex)
        }
    }

    LaunchedEffect(song) {
        song?.let {
            val colorInt = extractDominantColorFromRes(context, it.thumbnail)
            dominantColor = Color(colorInt)
            systemUiController.setStatusBarColor(Color(colorInt))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            progressThread?.interrupt()
        }
    }

    fun onNext() {
        if (currentSongIndex < songList.lastIndex) {
            playSongAtIndex(currentSongIndex + 1)
        }
    }

    fun onPrevious() {
        val newIndex = if (currentSongIndex - 1 < 0) songList.lastIndex else currentSongIndex - 1
        playSongAtIndex(newIndex)
    }

    song?.let {
        Scaffold(
            topBar = { TopMusicPlayerBar(onBackHome = onBackHome) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(dominantColor) // <--- MÀU NỀN Ở ĐÂY
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Spacer(Modifier.height(48.dp))

                    SongInfo(
                        image = it.thumbnail,
                        name = it.name,
                        artist = it.artist,
                        isAnimation = isAnimation
                    )

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
                                    isAnimation = false
                                } else {
                                    player.start()
                                    isPlaying = true
                                    isAnimation = true
                                    startUpdatingProgress(player)
                                }
                            }
                        },
                        onNext = { onNext() },
                        onPrevious = { onPrevious() },
                        onShuffleToggle = {},
                        onRepeatToggle = {}
                    )
                }
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

suspend fun extractDominantColorFromRes(context: Context, resId: Int): Int {
    val drawable = ContextCompat.getDrawable(context, resId)
    val bitmap = drawable?.toBitmap()
    val palette = bitmap?.let { Palette.from(it).generate() }

    return palette?.getDominantColor(AndroidColor.parseColor("#3d1e37"))
        ?: AndroidColor.parseColor("#3d1e37")
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Slider(
            value = progress,
            onValueChange = onSeekChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(currentTime, color = Color.White, modifier = Modifier.padding(start = 12.dp))
            Text(totalTime, color = Color.White, modifier = Modifier.padding(end = 12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onShuffleToggle) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffling) Color(0xFF9C27B0) else Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            IconButton(
                onClick = onPrevious,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(35.dp)
                )
            }

            IconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(70.dp)
                )
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(35.dp)
                )
            }

            IconButton(onClick = onRepeatToggle) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (isRepeating) Color(0xFF9C27B0) else Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun MusicControlPanelPreview() {
    ZingMP3CloneTheme {
        MusicControlPanel(
            currentTime = "01:23",
            totalTime = "03:45",
            progress = 0.35f,
            isPlaying = true,
            isShuffling = false,
            isRepeating = true,
            onSeekChanged = {},
            onPlayPause = {},
            onNext = {},
            onPrevious = {},
            onShuffleToggle = {},
            onRepeatToggle = {}
        )
    }
}

@Composable
fun TopMusicPlayerBar(modifier: Modifier = Modifier, onBackHome: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(36.dp)
                .clickable { onBackHome() }
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
            modifier = Modifier.size(28.dp)
        )
    }
}

//@Preview
//@Composable
//private fun TopMusicPlayerBarPreview() {
//    ZingMP3CloneTheme {
//        TopMusicPlayerBar()
//    }
//}

@Composable
fun SongInfo(
    modifier: Modifier = Modifier,
    image: Int,
    name: String,
    artist: String,
    isAnimation: Boolean
) {
    val rotation = remember { Animatable(0f) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimation) {
        if (isAnimation && !isRunning) {
            isRunning = true
            while (isRunning) {
                rotation.animateTo(
                    targetValue = rotation.value + 360f,
                    animationSpec = tween(durationMillis = 20000, easing = LinearEasing)
                )
                rotation.snapTo(rotation.value % 360f)
                if (!isAnimation) {
                    isRunning = false
                }
            }
        } else if (!isAnimation) {
            isRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .rotate(rotation.value),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .weight(0.1f),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(56.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = artist,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            var isFavorite by remember { mutableStateOf(false) }

            IconButton(
                onClick = { isFavorite = !isFavorite }
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .weight(0.1f),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
private fun SongInfoPreview() {
    ZingMP3CloneTheme {
        SongInfo(
            image = R.drawable.kim_phut_kim_gio,
            name = "KIM PHUT KIM GIO",
            artist = "ANH TRAI SAY HI",
            isAnimation = true
        )
    }
}