package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object OmdbHelper {
    private const val API_KEY = "bbb54f5d"

    // Map common Turkish titles to English for perfect OMDb queries
    fun mapShowNameForQuery(name: String): String {
        return when (name.trim().lowercase()) {
            "sürekli dizi" -> "Regular Show"
            "macera zamanı" -> "Adventure Time"
            "kafadar ayılar" -> "We Bare Bears"
            "gumball", "gumball özel" -> "The Amazing World of Gumball"
            "esrarengiz kasaba" -> "Gravity Falls"
            "samuray jack" -> "Samurai Jack"
            "generator rex" -> "Generator Rex"
            "ben 10" -> "Ben 10"
            "powerpuff girls", "the powerpuff girls" -> "The Powerpuff Girls"
            "johnny bravo" -> "Johnny Bravo"
            "ninjago" -> "Ninjago: Masters of Spinjitzu"
            "star wars: klon savaşları", "klon savaşları" -> "Star Wars: The Clone Wars"
            "adventure time" -> "Adventure Time"
            else -> {
                // Remove trailing tags like "Sezon", "Bölüm" or "Özel" to search for the pure show name
                var cleaned = name
                val tags = listOf("özel", "türkçe", "dublaj", "1.sezon", "2.sezon", "3.sezon")
                for (tag in tags) {
                    if (cleaned.lowercase().contains(tag)) {
                        cleaned = cleaned.replace(Regex("(?i)\\b$tag\\b"), "").trim()
                    }
                }
                cleaned
            }
        }
    }

    suspend fun fetchShowDetails(showName: String): ShowDetailsEntity? = withContext(Dispatchers.IO) {
        val queryName = mapShowNameForQuery(showName)
        val urlString = "https://www.omdbapi.com/?apikey=$API_KEY&t=${URLEncoder.encode(queryName, "UTF-8")}"
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d("OMDB", "Response for $showName (query: $queryName): $jsonText")
                val jsonObject = Json { ignoreUnknownKeys = true }.parseToJsonElement(jsonText).jsonObject
                val responseTag = jsonObject["Response"]?.jsonPrimitive?.content ?: "False"
                
                if (responseTag.equals("True", true)) {
                    val title = jsonObject["Title"]?.jsonPrimitive?.content ?: showName
                    val poster = jsonObject["Poster"]?.jsonPrimitive?.content ?: ""
                    val englishPlot = jsonObject["Plot"]?.jsonPrimitive?.content ?: ""
                    val rating = jsonObject["imdbRating"]?.jsonPrimitive?.content ?: ""
                    val genre = jsonObject["Genre"]?.jsonPrimitive?.content ?: ""
                    val year = jsonObject["Year"]?.jsonPrimitive?.content ?: ""
                    val actors = jsonObject["Actors"]?.jsonPrimitive?.content ?: ""
                    
                    // Direct quick translate checks
                    val translatedPlot = if (englishPlot.isNotEmpty() && !englishPlot.equals("N/A", true)) {
                        translateToTurkish(englishPlot).ifEmpty { englishPlot }
                    } else {
                        "Açıklama bulunamadı."
                    }
                    
                    val translatedGenre = if (genre.isNotEmpty() && !genre.equals("N/A", true)) {
                        translateToTurkish(genre).ifEmpty { genre }
                    } else {
                        genre
                    }
                    
                    // Save poster URL only if it's a valid link
                    val finalPoster = if (poster.startsWith("http")) poster else ""

                    return@withContext ShowDetailsEntity(
                        showName = showName, // keep original m3u show name as database key
                        posterUrl = finalPoster,
                        plot = translatedPlot,
                        rating = rating,
                        genre = translatedGenre,
                        year = year,
                        actors = actors
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("OMDB", "Error fetching OMDB for $showName", e)
        }
        null
    }

    suspend fun translateToTurkish(text: String): String = withContext(Dispatchers.IO) {
        if (text.isEmpty() || text.equals("N/A", true)) return@withContext ""
        try {
            val urlString = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=tr&dt=t&q=${URLEncoder.encode(text, "UTF-8")}"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = Json.parseToJsonElement(jsonText).jsonArray
                val partsArray = jsonArray.getOrNull(0)?.jsonArray
                if (partsArray != null) {
                    val sb = java.lang.StringBuilder()
                    for (i in 0 until partsArray.size) {
                        val part = partsArray.getOrNull(i)?.jsonArray
                        val translatedText = part?.getOrNull(0)?.jsonPrimitive?.content
                        if (!translatedText.isNullOrEmpty()) {
                            sb.append(translatedText)
                        }
                    }
                    return@withContext sb.toString().trim()
                }
            }
        } catch (e: Exception) {
            Log.e("OMDB", "Translation error for: $text", e)
        }
        ""
    }
}
