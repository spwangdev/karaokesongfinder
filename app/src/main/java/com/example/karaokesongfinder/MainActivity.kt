package com.example.karaokesongfinder

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.karaokesongfinder.ui.theme.KaraokeSongFinderTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import entities.api.Song
import viewmodels.SongSearchViewModel

class MainActivity : ComponentActivity() {
    private val songSearchViewModel = SongSearchViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            KaraokeSongFinderTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    KaraokeSearchScreen()
//                }
//            }
            KaraokeSongFinderTheme() {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val keyboardController = LocalSoftwareKeyboardController.current
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                            .padding(top = 12.dp)
                    ) {
                        // 1. Search Inputs
                        OutlinedTextField(
                            value = songSearchViewModel.searchQuery,
                            onValueChange = { nextText -> songSearchViewModel.searchQuery = nextText },
                            label = { Text("Search by Song or Artist") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 2. The Diagnostic Reader (Always visible!)
                        Text(text = "DEBUG - Query: ${songSearchViewModel.searchQuery}")
                        Text(text = "DEBUG - Loading: ${songSearchViewModel.isLoading}")

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                if (songSearchViewModel.searchQuery.isNotBlank()){
                                    songSearchViewModel.performSearch()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Find Karaoke Tracks")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Loading Indicator or Results List
                        if (songSearchViewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            Log.d("karaoke", "loading thing")
//        } else {
//            LazyColumn(
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier.fillMaxSize()
//            ) {
//                items(viewModel.songList.value) { song ->
//                    SongRow(song = song)
//                }
//            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KaraokeSearchScreen(viewModel: SongSearchViewModel = SongSearchViewModel()) {
    var localSearchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    Log.d("karaoke", "redrawn screen ${viewModel.isLoading}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .padding(top = 12.dp)
    ) {
        // 1. Search Inputs
        OutlinedTextField(
            value = localSearchQuery,
            onValueChange = { nextText -> localSearchQuery = nextText },
            label = { Text("Search by Song or Artist") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. The Diagnostic Reader (Always visible!)
        Text(text = "DEBUG - Query: ${viewModel.searchQuery}")
        Text(text = "DEBUG - Loading: ${viewModel.isLoading}")

        Button(
            onClick = {
                keyboardController?.hide()
                if (localSearchQuery.isNotBlank()){
                    viewModel.performSearch()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find Karaoke Tracks")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Loading Indicator or Results List
        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Log.d("karaoke", "loading thing")
//        } else {
//            LazyColumn(
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier.fillMaxSize()
//            ) {
//                items(viewModel.songList.value) { song ->
//                    SongRow(song = song)
//                }
//            }
        }
    }
}

@Composable
fun SongRow(song: Song) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = song.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "by ${song.singer}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KaraokeSongFinderTheme {
        Greeting("Android")
    }
}