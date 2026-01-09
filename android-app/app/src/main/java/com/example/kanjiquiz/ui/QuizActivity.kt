package com.example.kanjiquiz.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.kanjiquiz.app.App
import com.example.kanjiquiz.ui.theme.KanjiQuizTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KanjiQuizTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier) {
    ScreenCard(modifier) {
        Text("TODO: Settings Screen", fontSize = 50.sp, modifier = Modifier.fillMaxWidth())
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("KanjiQuiz") },
                actions = {
                    IconButton(onClick = {
                        val currentRoute = navController.currentBackStackEntry?.destination?.route
                        if (currentRoute != "Settings")
                            navController.navigate("Settings")
                        else
                            navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        NavHost(
            navController,
            navController.createGraph("Quiz") {
                composable("Quiz") { backStackEntry ->
                    val context = LocalContext.current
                    val app = context.applicationContext as App

                    val viewModel: MainViewModel = viewModel(
                        viewModelStoreOwner = backStackEntry,
                        factory = QuizViewModelFactory(app.vocabRepository)
                    )

                    QuizScreen(viewModel, modifier)
                }
                composable("Settings") { backStackEntry ->
                    SettingsScreen(modifier)
                }
            })
    }
}

/*
@Preview(showBackground = true)
@Composable
fun Preview() {
    KanjiQuizTheme {
        val viewModel = MainViewModel(object : VocabRepository {
            override suspend fun getAll(): List<VocabEntry> = listOf(
                VocabEntry(
                    id = 1,
                    expression = "犬",
                    reading = "いぬ",
                    meaning = "dog",
                    vocabSet = "jlpt-n5"
                )
            )
        })
        MainScreen(viewModel)
    }
}

 */