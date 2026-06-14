package viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import entities.api.Song
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import services.api.KaraokeApiService
import kotlin.collections.emptyList
import kotlin.math.log

class SongSearchViewModel : ViewModel() {
    // UI States
    var searchQuery by mutableStateOf("")
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
    fun performSearch(searchQuery: String) {
        Log.d("KaraokeTest","inside perform search: $searchQuery")
//        if (searchQuery.isBlank()) return
//
//        viewModelScope.launch {
//            isLoading.value = true
//            try {
//                val results = apiService.searchSongs(searchQuery)
//                songList.value = results
//                // Handle errors gracefully in production
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                isLoading.value = false
//            }
//        }
    }
}