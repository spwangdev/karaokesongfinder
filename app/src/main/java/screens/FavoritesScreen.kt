package screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodels.SongSearchViewModel

@Composable
fun FavoritesScreen(viewModel: SongSearchViewModel) {
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    var filterQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredSongs = remember(favoriteSongs, filterQuery) {
        if (filterQuery.isBlank()) {
            favoriteSongs
        } else {
            favoriteSongs.filter {
                it.title.contains(filterQuery, ignoreCase = true) ||
                        it.singer.contains(filterQuery, ignoreCase = true) ||
                        it.no.contains(filterQuery)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Your saved songs",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            maxLines = 1,
            modifier = Modifier.padding(bottom = 1.dp)
        )

        if (favoriteSongs.isNotEmpty()) {
            OutlinedTextField(
                value = filterQuery,
                onValueChange = { filterQuery = it },
                label = { Text("Filter by song, artist, or no.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                trailingIcon = {
                    if (filterQuery.isNotEmpty()) {
                        IconButton(onClick = { filterQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
        }

        if (favoriteSongs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No saved songs yet.", color = Color.Gray)
            }
        } else if (filteredSongs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No songs match your filter.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = 90.dp) // Passes right under floating glass nav bar
            ) {
                items(filteredSongs) { savedSong ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {

                            Column(modifier = Modifier.fillMaxWidth().padding(end = 75.dp)) {
                                Text(text = savedSong.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = "Artist: ${savedSong.singer}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Text(text = "No. ${savedSong.no}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }

                            Row(
                                modifier = Modifier.align(Alignment.BottomEnd),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.removeFromFavorites(savedSong) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Song Number", savedSong.no)
                                        clipboard.setPrimaryClip(clip)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}