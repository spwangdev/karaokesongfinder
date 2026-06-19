package viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import database.AppDatabase
import database.SavedSong
import entities.api.Song
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import services.api.KaraokeApiService
import kotlin.collections.emptyList
import kotlin.math.log
import kotlin.time.Duration.Companion.milliseconds

class SongSearchViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.savedSongDao()

    val favoriteSongs: StateFlow<List<SavedSong>> = dao.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI States
    var searchQuery by mutableStateOf("")
    var songList by mutableStateOf<List<Song>>(emptyList())
    var isLoading by mutableStateOf(false)

    var hasSearched by mutableStateOf(false)
    var hasNetwork by mutableStateOf(true)

    // Latest Songs UI States
    private val _latestSongs = MutableStateFlow<List<Song>>(emptyList())
    val latestSongs = _latestSongs.asStateFlow()

    private val _isLatestLoading = MutableStateFlow(false)
    val isLatestLoading = _isLatestLoading.asStateFlow()

    private var lastFetchedDate: String? = null

    // Initialize Retrofit
    private val apiService: KaraokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.manana.kr/karaoke/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KaraokeApiService::class.java)
    }

    // Function called when user clicks "Search"
    fun performSearch() {
        isLoading = true
        hasSearched = false // Reset searched state at start
        songList = emptyList() // Clear previous results immediately
        
        viewModelScope.launch {
            try {
                val results = apiService.searchSongs(searchQuery)
                // Filter out any potential nulls or malformed results if necessary,
                // but primarily just assign what the API returns.
                songList = results ?: emptyList()
                hasSearched = true
            } catch (e: Exception) {
                songList = emptyList()
                hasSearched = true // Still set to true so "No results" or error could be shown
            } finally {
                isLoading = false
            }
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Get the currently active network configuration
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        // Verify that the network has actual internet transit capability
        Log.d("karaoke", capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).toString())
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isSongFavorited(songNo: String): Boolean {
        return favoriteSongs.value.any { it.no == songNo }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            val savedSong = SavedSong(no = song.no, title = song.title, singer = song.singer)
            if (isSongFavorited(song.no)) {
                dao.deleteFavorite(savedSong)
            } else {
                dao.insertFavorite(savedSong)
            }
        }
    }

    fun removeFromFavorites(savedSong: SavedSong) {
        viewModelScope.launch {
            dao.deleteFavorite(savedSong)
        }
    }

    fun fetchLatestSongs(date: String) {
        if (date == lastFetchedDate && _latestSongs.value.isNotEmpty()) return

        _isLatestLoading.value = true
        viewModelScope.launch {
            try {
                val results = apiService.getLatestSongs(date)
                // Filter only for brand "tj"
                _latestSongs.value = results.filter { it.brand.equals("tj", ignoreCase = true) }
                lastFetchedDate = date
            } catch (e: Exception) {
                Log.e("SongSearchViewModel", "Failed to fetch latest songs", e)
                _latestSongs.value = emptyList()
                lastFetchedDate = null
            } finally {
                _isLatestLoading.value = false
            }
        }
    }
}