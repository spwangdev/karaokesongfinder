package screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodels.SongSearchViewModel
import java.util.*

@Composable
fun LatestSongsScreen(viewModel: SongSearchViewModel) {
    var expandedYear by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }
    
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear downTo 2010).toList()
    val months = (1..12).toList()
    
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }

    val songList by viewModel.latestSongs.collectAsState()
    val isLoading by viewModel.isLatestLoading.collectAsState()

    // Trigger initial load and when selection changes
    LaunchedEffect(selectedYear, selectedMonth) {
        val dateString = String.format("%04d%02d", selectedYear, selectedMonth)
        viewModel.fetchLatestSongs(dateString)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .padding(top = 8.dp)
    ) {
        Text(
            text = "Latest Songs",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            maxLines = 1,
            modifier = Modifier.padding(bottom = 5.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Year Selector
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { expandedYear = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("$selectedYear")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expandedYear,
                    onDismissRequest = { expandedYear = false }
                ) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString()) },
                            onClick = {
                                selectedYear = year
                                expandedYear = false
                            }
                        )
                    }
                }
            }

            // Month Selector
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { expandedMonth = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(String.format("%02d", selectedMonth))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expandedMonth,
                    onDismissRequest = { expandedMonth = false }
                ) {
                    months.forEach { month ->
                        DropdownMenuItem(
                            text = { Text(String.format("%02d", month)) },
                            onClick = {
                                selectedMonth = month
                                expandedMonth = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (songList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No songs found for this period.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(songList) { song ->
                    LatestSongRow(song = song, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun LatestSongRow(song: entities.api.Song, viewModel: SongSearchViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
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
                    .padding(end = 150.dp)
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
                            context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Song Number", song.no)
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
