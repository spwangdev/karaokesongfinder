package services.api

import entities.api.Song
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KaraokeApiService {
    @GET("{searchTerm}/tj.json")
    suspend fun searchSongs(
        @Path("searchTerm") query: String
    ): List<Song>
}