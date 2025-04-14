package com.example.zingmp3clone.home.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.zingmp3clone.R
import com.example.zingmp3clone.data.model.RecentSong
import com.example.zingmp3clone.ui.theme.ZingMP3CloneTheme
import com.example.zingmp3clone.viewmodel.RecentSongViewModel

@Composable
fun RecentSongScreen(
    modifier: Modifier = Modifier,
    onRecentSongClick: (Int) -> Unit,
    navController: NavController
) {
    val recentSongViewModel: RecentSongViewModel = hiltViewModel()
    val recentSongs = recentSongViewModel.recentSong.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        Icons.Default.KeyboardDoubleArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.width(4.dp))

                Text(
                    "Nghe gần đây",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FiveSongs()

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(start = 8.dp, top = 8.dp),
                contentPadding = PaddingValues(vertical = 1.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val songs = recentSongs.value
                if (songs.isEmpty()) {
                    item {
                        Text(
                            text = "Chưa có bài hát nào",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(songs, key = { it.id }) { song ->
                        RecentSongCard(
                            recentSong = song,
                            onSongClick = { onRecentSongClick(song.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSongCard(
    modifier: Modifier = Modifier,
    recentSong: RecentSong,
    isSelected: Boolean = false,
    onSongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onSongClick() },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = recentSong.thumbnail),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = recentSong.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )

                Text(
                    text = recentSong.artist,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight =20.sp
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
private fun RecentSongCardPreview() {
    ZingMP3CloneTheme {
        RecentSongCard(
            recentSong = RecentSong(
                id = R.raw.kim_phut_kim_gio,
                name = "KIM PHÚT KIM GIỜ",
                artist = "ANH TRAI \"SAY HI\", HIEUTHUHAI, HURRYKNG, NEGAV, PHÁP KIỀU",
                duration = 393,
                thumbnail = R.drawable.kim_phut_kim_gio
            ),
            onSongClick = {}
        )
    }
}

@Composable
fun FiveSongs(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(1.dp, color = MaterialTheme.colorScheme.onSurface, RoundedCornerShape(16.dp))
    ) {
        Text(
            "5 bài hát đã nghe gần đây",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}