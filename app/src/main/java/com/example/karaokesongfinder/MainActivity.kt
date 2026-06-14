package com.example.karaokesongfinder

import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import entities.api.Song
import viewmodels.SongSearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaraokeSongFinderTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This is where the magic happens! Call your screen here:
                    KaraokeSearchScreen()
                }
            }
        }
    }
}

@Composable
fun KaraokeSearchScreen(viewModel: SongSearchViewModel = SongSearchViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .padding(top = 12.dp)
    ) {
        // 1. Search Inputs
        OutlinedTextField(
            value = viewModel.searchQuery.value,
            onValueChange = { nextText -> viewModel.searchQuery.value = nextText },
            label = { Text("Search by Song or Artist") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.performSearch() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find Karaoke Tracks")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Loading Indicator or Results List
        if (viewModel.isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewModel.songList.value) { song ->
                    SongRow(song = song)
                }
            }
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