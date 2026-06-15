package viewmodels

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
import entities.api.Song
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import services.api.KaraokeApiService
import kotlin.collections.emptyList
import kotlin.math.log
import kotlin.time.Duration.Companion.milliseconds

class SongSearchViewModel : ViewModel() {
    // UI States
    var searchQuery by mutableStateOf("")
    var songList by mutableStateOf<List<Song>>(emptyList())
    var isLoading by mutableStateOf(false)

    var hasSearched by mutableStateOf(false)
    var hasNetwork by mutableStateOf(true)

    // Initialize Retrofit
    private val apiService: KaraokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.manana.kr/karaoke/song/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KaraokeApiService::class.java)
    }

    // Function called when user clicks "Search"
    fun performSearch() {
        isLoading = true
        viewModelScope.launch {
            try {
                val results = apiService.searchSongs(searchQuery)
                songList = results
                hasSearched = true
            } catch (e: Exception) {
                e.printStackTrace()
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
}