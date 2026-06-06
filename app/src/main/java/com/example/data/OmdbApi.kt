package com.example.data

import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {
    @GET("/")
    suspend fun getShowDetails(
        @Query("t") title: String,
        @Query("apikey") apiKey: String = "bbb54f5d",
        @Query("type") type: String = "series"
    ): OmdbResponse
}

data class OmdbResponse(
    val Title: String?,
    val Year: String?,
    val Rated: String?,
    val Released: String?,
    val Runtime: String?,
    val Genre: String?,
    val Director: String?,
    val Writer: String?,
    val Actors: String?,
    val Plot: String?,
    val Language: String?,
    val Country: String?,
    val Awards: String?,
    val Poster: String?,
    val imdbRating: String?,
    val imdbVotes: String?,
    val imdbID: String?,
    val totalSeasons: String?,
    val Response: String?
)
