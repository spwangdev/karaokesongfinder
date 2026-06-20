package screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import database.SavedSong
import viewmodels.SongSearchViewModel

@Composable
fun FavoritesScreen(viewModel: SongSearchViewModel) {
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    var filterQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    var songToDelete by remember { mutableStateOf<SavedSong?>(null) }

    val filteredSongs = remember(favoriteSongs, filterQuery) {
        val filtered = if (filterQuery.isBlank()) {
            favoriteSongs
        } else {
            favoriteSongs.filter {
                it.title.contains(filterQuery, ignoreCase = true) ||
                        it.singer.contains(filterQuery, ignoreCase = true) ||
                        it.no.contains(filterQuery)
            }
        }
        filtered.sortedBy { it.title }
    }

    if (songToDelete != null) {
        AlertDialog(
            onDismissRequest = { songToDelete = null },
            title = { Text("Confirm Removal") },
            text = { Text("Are you sure you want to remove this song from your favourites?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        songToDelete?.let { viewModel.removeFromFavorites(it) }
                        songToDelete = null
                    }
                ) {
                    Text("Remove", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { songToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Your favourites",
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
                label = { Text("Filter by song, artist, or no.", fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
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
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(filteredSongs) { savedSong ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {

                            Column(modifier = Modifier.fillMaxWidth().padding(end = 75.dp)) {
                                Text(text = savedSong.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = "Artist: ${savedSong.singer}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Text(text = "No. ${savedSong.no}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                            }

                            Row(
                                modifier = Modifier.align(Alignment.BottomEnd),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { songToDelete = savedSong },
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