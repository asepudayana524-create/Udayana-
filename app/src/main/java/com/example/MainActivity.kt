package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AiRecommenderScreen
import com.example.ui.ExploreScreen
import com.example.ui.MovieDetailScreen
import com.example.ui.WatchlistScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MovieViewModel

sealed interface Screen {
    object Explore : Screen
    object Watchlist : Screen
    object AiChat : Screen
    data class MovieDetail(val movieId: String) : Screen
}

class MainActivity : ComponentActivity() {
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Explore) }
                // Persist the previous tabs when inspecting detail back
                var activeTab by remember { mutableStateOf(0) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_app_scaffold"),
                    bottomBar = {
                        // We hide the bottom bar when in MovieDetail screen for full immersive theatre view
                        if (currentScreen !is Screen.MovieDetail) {
                            NavigationBar(
                                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                NavigationBarItem(
                                    selected = activeTab == 0 && currentScreen is Screen.Explore,
                                    onClick = {
                                        activeTab = 0
                                        currentScreen = Screen.Explore
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "Jelajah"
                                        )
                                    },
                                    label = { Text("Jelajah", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("nav_explore")
                                )

                                NavigationBarItem(
                                    selected = activeTab == 1 && currentScreen is Screen.Watchlist,
                                    onClick = {
                                        activeTab = 1
                                        currentScreen = Screen.Watchlist
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = "Koleksi"
                                        )
                                    },
                                    label = { Text("Koleksi", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("nav_watchlist")
                                )

                                NavigationBarItem(
                                    selected = activeTab == 2 && currentScreen is Screen.AiChat,
                                    onClick = {
                                        activeTab = 2
                                        currentScreen = Screen.AiChat
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = "Tanya AI"
                                        )
                                    },
                                    label = { Text("Tanya AI", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("nav_ai")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "ScreenTransition"
                        ) { screen ->
                            when (screen) {
                                is Screen.Explore -> {
                                    ExploreScreen(
                                        viewModel = viewModel,
                                        onMovieClick = { id ->
                                            currentScreen = Screen.MovieDetail(id)
                                        }
                                    )
                                }
                                is Screen.Watchlist -> {
                                    WatchlistScreen(
                                        viewModel = viewModel,
                                        onMovieClick = { id ->
                                            currentScreen = Screen.MovieDetail(id)
                                        }
                                    )
                                }
                                is Screen.AiChat -> {
                                    AiRecommenderScreen(viewModel = viewModel)
                                }
                                is Screen.MovieDetail -> {
                                    MovieDetailScreen(
                                        movieId = screen.movieId,
                                        viewModel = viewModel,
                                        onBack = {
                                            // Back navigates back to whatever our active tab was
                                            currentScreen = when (activeTab) {
                                                1 -> Screen.Watchlist
                                                2 -> Screen.AiChat
                                                else -> Screen.Explore
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
