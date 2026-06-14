package viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import entities.api.Song
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import services.api.KaraokeApiService
import kotlin.collections.emptyList

class SongSearchViewModel : ViewModel() {
    // UI States
    var searchQuery = mutableStateOf("")
    var songList = mutableStateOf<List<Song>>(emptyList())
    var isLoading = mutableStateOf(false)

    // Initialize Retrofit
    //{0}/{1}.json
    private val apiService: KaraokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.manana.kr/karaoke/song/") // Replace with your actual API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KaraokeApiService::class.java)
    }

    // Function called when user clicks "Search"
    fun performSearch() {
        if (searchQuery.value.isBlank()) return

        viewModelScope.launch {
            isLoading.value = true
            try {
                val results = apiService.searchSongs(searchQuery.value)
                songList.value = results
                // Handle errors gracefully in production
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
}