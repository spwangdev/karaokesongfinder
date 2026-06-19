package services.api

import entities.api.Song
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KaraokeApiService {
    @GET("song/{searchTerm}/tj.json")
    suspend fun searchSongs(
        @Path("searchTerm") query: String
    ): List<Song>

    @GET("singer/{searchTerm}.json?brand=tj")
    suspend fun searchBySinger(
        @Path("searchTerm") query: String
    ): List<Song>

    @GET("release/{date}.json")
    suspend fun getLatestSongs(
        @Path("date") date: String
    ): List<Song>
}