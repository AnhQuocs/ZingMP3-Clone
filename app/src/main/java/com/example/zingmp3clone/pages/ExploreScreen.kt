package com.example.zingmp3clone.pages

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.util.Logger
import com.example.zingmp3clone.R
import com.example.zingmp3clone.item.SongCard
import com.example.zingmp3clone.ui.theme.PinkTopic
import com.example.zingmp3clone.viewmodel.SongViewModel

@Composable
fun ExploreScreen(
    onSongClick: (Int) -> Unit,
    navController: NavController
) {

    val songViewModel: SongViewModel = hiltViewModel()

    Scaffold(
        topBar = { TopAppBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                Text(
                    "Gợi ý cho bạn",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                MySongs(songViewModel = songViewModel, onSongClick = onSongClick)
            }

            item {
                RecentSong(navController = navController)
            }

            item {
                ForFan()
            }
        }
    }
}

@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Khám phá",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .border(0.1.dp, PinkTopic, RoundedCornerShape(12.dp))
                .background(color = Color(0xFF1E1B26), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Palette,
                    contentDescription = null,
                    tint = PinkTopic,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(2.dp))

                Text(
                    "Chủ đề",
                    fontSize = 14.sp
                )
            }
        }

        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.MicNone,
                contentDescription = "Toggle search",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Toggle search",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MySongs(
    songViewModel: SongViewModel,
    onSongClick: (Int) -> Unit
) {
    val songList by songViewModel.songs.collectAsState(initial = emptyList())
    val pages = songList.chunked(3)
    val pagerState = rememberPagerState(pageCount = { pages.size })

    HorizontalPager(state = pagerState) { page ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            pages[page].forEach { song ->
                SongCard(
                    song = song,
                    onClick = { onSongClick(song.id) }
                )
            }
        }
    }
}

@Composable
fun RecentSong(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Nghe gần đây",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(12.dp))

        LazyRow(
//            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Log.d("Recompose", "recompose")
            item {
                Column(
                    modifier = Modifier.width(90.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.recent),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                navController.navigate("recent")
                            }
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Bài Hát Nghe Gần Đây",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }

            item {
                PlayListItem(image = R.drawable.playlist, title = "Playlist")
            }

            item {
                PlayListItem(
                    image = R.drawable.denvau,
                    title = "Những bài hát hay nhất của Đen Vâu"
                )
            }

            item {
                PlayListItem(image = R.drawable.vpop, title = "V-Pop của năm 2025")
            }

            item {
                PlayListItem(image = R.drawable.atsh, title = "Anh Trai \"Say Hi\"")
            }

            item {
                PlayListItem(image = R.drawable.chart, title = "#zingclchart")
            }
        }
    }
}

@Composable
fun PlayListItem(
    image: Int,
    title: String
) {
    Column(
        modifier = Modifier.width(90.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp)
                    .fillMaxWidth(0.28f)
            ) {
                Icon(
                    Icons.Default.PlayCircleFilled,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 13.sp,
            lineHeight = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}

@Composable
fun ForFan() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.denvauavt),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.12f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(4.dp))

            Column {
                Text(
                    "Dành cho fan",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight = 16.sp
                )

                Text(
                    "Đen Vâu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ForFanItem(image = R.drawable.denvau, title = "Những bài hát hay nhất của Đen Vâu")
            }

            item {
                ForFanItem(image = R.drawable.mang_tien_ve_cho_me, title = "Mang tiền về cho mẹ")
            }

            item {
                ForFanItem(image = R.drawable.mot_trieu_like, title = "Một triệu like")
            }

            item {
                ForFanItem(image = R.drawable.muoi_nam, title = "Mười năm")
            }

            item {
                ForFanItem(image = R.drawable.loi_nho, title = "Lối nhỏ")
            }

            item {
                ForFanItem(image = R.drawable.nau_an_cho_em, title = "Nấu ăn cho em")
            }

            item {
                ForFanItem(image = R.drawable.di_ve_nha, title = "Đi về nhà (Đen x JustaTee)")
            }
        }
    }
}

@Composable
fun ForFanItem(image: Int, title: String) {
    Column(
        modifier = Modifier.width(150.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .aspectRatio(1f)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = title,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}

@Preview
@Composable
private fun ForFanItemPreview() {
    MaterialTheme(darkColorScheme()) {
        ForFan()
    }
}

@Preview
@Composable
private fun PlayListItemPreview() {
    MaterialTheme(darkColorScheme()) {
        TopAppBar()
    }
}