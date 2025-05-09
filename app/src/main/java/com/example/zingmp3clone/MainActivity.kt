package com.example.zingmp3clone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.zingmp3clone.home.HomeScreen
import com.example.zingmp3clone.home.tabs.MusicPlayerScreen
import com.example.zingmp3clone.home.tabs.RecentSongScreen
import com.example.zingmp3clone.ui.theme.ZingMP3CloneTheme
import com.example.zingmp3clone.viewmodel.RecentSongViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZingMP3CloneTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Greeting(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    recentSongViewModel: RecentSongViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable(
            route = "home"
        ) {
            HomeScreen(
                onSongClick = { songId ->
                    navController.navigate("musicPlayer/$songId")
                    recentSongViewModel.insertRecentSongById(songId)
                },
                navController = navController
            )
        }

        composable(
            route = "musicPlayer/{songId}",
            arguments = listOf(navArgument("songId") { type = NavType.IntType }),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeOut(animationSpec = tween(durationMillis = 500))
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(animationSpec = tween(durationMillis = 500))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeOut(animationSpec = tween(durationMillis = 500))
            }
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getInt("songId")
            MusicPlayerScreen(modifier = Modifier.fillMaxSize(), songId = songId, onBackHome = {navController.popBackStack()})
        }

        composable("recent") {
            RecentSongScreen(
                onRecentSongClick = { songId ->
                    navController.navigate("musicPlayer/$songId")
                    recentSongViewModel.insertRecentSongById(songId)
                },
                navController = navController
            )
        }
    }
}