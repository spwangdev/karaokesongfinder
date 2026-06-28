package screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    .padding(12.dp)
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "Karaoke Song Finder",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
                OutlinedTextField(
                    value = songSearchViewModel.searchQuery,
                    onValueChange = { nextText -> songSearchViewModel.searchQuery = nextText },
                    label = { Text(if (songSearchViewModel.searchBySinger) "Search by Artist" else "Search by Song") },
                    modifier = Modifier.fillMaxWidth(),
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            songSearchViewModel.hasNetwork =
                                songSearchViewModel.isNetworkAvailable(context)

                            if (songSearchViewModel.hasNetwork && songSearchViewModel.searchQuery.isNotBlank()) {
                                songSearchViewModel.performSearch()
                            }
                        }
                    ),

                    trailingIcon = {
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !songSearchViewModel.searchBySinger,
                        onClick = { songSearchViewModel.searchBySinger = false },
                        label = { Text("Song") },
                        leadingIcon = if (!songSearchViewModel.searchBySinger) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null
                    )
                    FilterChip(
                        selected = songSearchViewModel.searchBySinger,
                        onClick = { songSearchViewModel.searchBySinger = true },
                        label = { Text("Artist") },
                        leadingIcon = if (songSearchViewModel.searchBySinger) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
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
                            SongRow(song = song, songSearchViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongRow(song: Song, viewModel: SongSearchViewModel) {
    val context = LocalContext.current
    val favoriteList by viewModel.favoriteSongs.collectAsState()
    val isFavorited = favoriteList.any { it.no == song.no }
    var showInfoModal by remember { mutableStateOf(false) }

    if (showInfoModal) {
        AlertDialog(
            onDismissRequest = { showInfoModal = false },
            title = { Text(text = "Song Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoItem(label = "Title", value = song.title)
                    InfoItem(label = "Artist", value = song.singer)
                    InfoItem(label = "Release", value = song.release)
                    InfoItem(label = "Brand", value = song.brand)
                    InfoItem(label = "Composer", value = song.composer)
                    InfoItem(label = "Lyricist", value = song.lyricist)
                    InfoItem(label = "No.", value = song.no)
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoModal = false }) {
                    Text("Close")
                }
            }
        )
    }

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
                    .padding(end = 150.dp) // Leave space for buttons
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
                    text = "Released: ${song.release}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "No. ${song.no}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedIconButton(
                    onClick = { showInfoModal = true },
                    modifier = Modifier.size(44.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Song Info",
                        modifier = Modifier.size(24.dp)
                    )
                }

                OutlinedIconButton(
                    onClick = { viewModel.toggleFavorite(song) },
                    modifier = Modifier.size(44.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        contentColor = if (isFavorited) Color(0xFFE53E3E) else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        modifier = Modifier.size(24.dp)
                    )
                }

                OutlinedIconButton(
                    onClick = {
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Song Details", "${song.title} - ${song.singer} (${song.no})")
                        clipboard.setPrimaryClip(clip)
                    },
                    modifier = Modifier.size(44.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Song Number",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value.ifBlank { "N/A" },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}