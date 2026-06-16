package screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.karaokesongfinder.ui.theme.KaraokeSongFinderTheme
import entities.api.Song
import viewmodels.SongSearchViewModel

@Composable
fun SongSearchScreen(
    songSearchViewModel: SongSearchViewModel,
    onNavigateToFavorites: () -> Unit
) {
    KaraokeSongFinderTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            val context = LocalContext.current
            val focusManager = LocalFocusManager.current
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
                    modifier = Modifier.fillMaxWidth(),

                    trailingIcon = {
                        // Only show the 'X' button if the user has actually typed something
                        if (songSearchViewModel.searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                songSearchViewModel.searchQuery = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search text"
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. The Diagnostic Reader (Always visible!)
                Text(text = "DEBUG - Query: ${songSearchViewModel.searchQuery}")
                Text(text = "DEBUG - Loading: ${songSearchViewModel.isLoading}")
                Text(text = "DEBUG - Network: ${songSearchViewModel.hasNetwork}")

                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        songSearchViewModel.hasNetwork =
                            songSearchViewModel.isNetworkAvailable(context)

                        if (songSearchViewModel.hasNetwork && songSearchViewModel.searchQuery.isNotBlank()) {
                            songSearchViewModel.performSearch()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Search karaoke songs")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Loading Indicator or Results List
                if (!songSearchViewModel.hasNetwork) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No network connectivity.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red
                        )
                    }
                } else if (songSearchViewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    Log.d("karaoke", "loading thing")
                } else if (songSearchViewModel.hasSearched && songSearchViewModel.songList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        items(songSearchViewModel.songList) { song ->
                            SongRow(song = song)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongRow(song: Song) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Artist: ${song.singer}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "No. ${song.no}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = {
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Song Number", song.no.toString())
                    clipboard.setPrimaryClip(clip)
                    //Toast.makeText(context, "Copied song number: ${song.no}", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Song Number",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}