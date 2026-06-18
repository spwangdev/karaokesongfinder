package screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
                    SongRow(song = song, viewModel = viewModel)
                }
            }
        }
    }
}
