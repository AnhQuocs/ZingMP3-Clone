package com.example.zingmp3clone.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zingmp3clone.pages.ExploreScreen
import com.example.zingmp3clone.pages.FavoriteScreen
import com.example.zingmp3clone.pages.LibraryScreen
import com.example.zingmp3clone.pages.ProfileScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSongClick: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 4 }
    )

    val coroutineScope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()
    // Lấy colorScheme trong composable context
    val surfaceColor = MaterialTheme.colorScheme.surface

    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(
            color = surfaceColor, // Sử dụng biến đã lưu
            darkIcons = false // hoặc false tùy màu nền
        )
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                selectedIndex = pagerState.currentPage,
                onItemSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) { page ->
                when (page) {
                    0 -> LibraryScreen()
                    1 -> ExploreScreen(onSongClick = onSongClick)
                    2 -> FavoriteScreen()
                    3 -> ProfileScreen()
                }
            }

        }
    }
}

@Composable
fun BottomAppBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        "Thư viện" to Icons.Default.LibraryMusic,
        "Khám phá" to Icons.Default.Explore,
        "Yêu thích" to Icons.Default.Favorite,
        "Cá nhân" to Icons.Default.Person
    )

    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val strokeWidth = 0.5.dp

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .height(56.dp)
            .drawBehind {
                drawLine(
                    color = borderColor, // sử dụng biến đã lấy từ composable context
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth.toPx() // toPx() dùng được ở đây
                )
            }
    ) {
        items.forEachIndexed { index, item ->
            val interactionSource = remember { MutableInteractionSource() }

            NavigationBarItem(
                modifier = Modifier.padding(top = 8.dp),
                selected = selectedIndex == index,
                onClick = {onItemSelected(index)},
                icon = {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first,
                        modifier = Modifier.size(24.dp),
                        tint = if (selectedIndex == index) Color(0xFF6600FF) else MaterialTheme.colorScheme.onSurface
                    )
                },
                label = {
                    Text(
                        text = item.first,
                        style = TextStyle(
                            color = if (selectedIndex == index) Color(0xFF6600FF) else MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                },
                interactionSource = interactionSource, // ngăn ripple ở đây
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // không có hiệu ứng nền khi được chọn
                )
            )
        }
    }
}

@Preview
@Composable
private fun BottomAppBarPreview() {
    MaterialTheme(darkColorScheme()) {
        BottomAppBar(
            selectedIndex = 0,
            onItemSelected = {}
        )
    }
}

