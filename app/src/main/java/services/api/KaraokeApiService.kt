package services.api

import entities.api.Song
import retrofit2.http.GET
import retrofit2.http.Query

interface KaraokeApiService {
    @GET("search") // Your specific API endpoint path
    suspend fun searchSongs(
        @Query("query") query: String // e.g., ?query=Bohemian+Rhapsody
    ): List<Song>
}