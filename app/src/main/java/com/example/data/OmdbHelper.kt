package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject
import org.json.JSONArray

object OmdbHelper {
    private const val API_KEY = "bbb54f5d"

    // Map common Turkish titles to English for perfect OMDb queries
    fun mapShowNameForQuery(name: String): String {
        return when (name.trim().lowercase()) {
            "bizim için şampiyon", "bizim i̇çin şampiyon" -> "Bizim Icin Sampiyon"
            "bi küçük eylül meselesi" -> "Bi Kucuk Eylul Meselesi"
            "istanbul kırmızısı", "i̇stanbul kırmızısı" -> "Red Istanbul"
            "bir aşk iki hayat" -> "Bir Ask Iki Hayat"
            "özgür dünya" -> "Ozgur Dunya"
            "gişe memuru" -> "Toll Booth"
            "cebimdeki yabancı" -> "Stranger in My Pocket"
            "yola geldik" -> "Yola Geldik"
            "8 saniye" -> "8 Seconds"
            "silsile" -> "Silsile"
            "saklı yüzler" -> "Sakli Yuzler"
            "gergedan mevsimi" -> "Rhino Season"
            "büyük adam küçük aşk" -> "Hejar"
            "beynelmilel" -> "The International"
            "anons" -> "The Announcement"
            "annemin yarası" -> "My Mother's Wound"
            "i̇ncir reçeli 2", "incir reçeli 2" -> "Incir Receli 2"
            "napoli'nin sırrı" -> "Naples in Veils"
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
            "kral şakir", "kral sakir" -> "Kral Sakir"
            "rafadan tayfa" -> "Rafadan Tayfa"
            else -> {
                // Remove trailing tags like "Sezon", "Bölüm" or "Özel" to search for the pure show name
                var cleaned = name
                val tags = listOf(
                    "özel", "türkçe", "dublaj", "1.sezon", "2.sezon", "3.sezon", "4.sezon", "5.sezon", "6.sezon", "7.sezon", "8.sezon", "9.sezon", "10.sezon",
                    "bölüm", "HD", "belgeseli", "belgesel", "programı", "filmi", "dizisi", "izle", "full", "seyret"
                )
                for (tag in tags) {
                    cleaned = cleaned.replace(Regex("(?i)\\b$tag\\b"), "").trim()
                }
                
                // Keep alphanumeric and basic spacing
                cleaned = cleaned.replace(Regex("[\\-\\–\\—\\,\\.\\\"\\']"), " ").trim()
                cleaned = cleaned.replace(Regex("\\s+"), " ")
                cleaned
            }
        }
    }

    private suspend fun executeQuery(urlString: String): JSONObject? = withContext(Dispatchers.IO) {
        try {
            Log.d("OMDB", "Querying: $urlString")
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.connectTimeout = 5500
            connection.readTimeout = 5500
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonText)
                val responseTag = jsonObject.optString("Response", "False")
                if (responseTag.equals("True", true)) {
                    return@withContext jsonObject
                }
            }
        } catch (e: Exception) {
            Log.e("OMDB", "Error executing: $urlString", e)
        }
        null
    }

    suspend fun translateToEnglish(text: String): String = withContext(Dispatchers.IO) {
        if (text.isEmpty() || text.equals("N/A", true)) return@withContext ""
        try {
            val urlString = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=tr&tl=en&dt=t&q=${URLEncoder.encode(text, "UTF-8")}"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                val outerArray = JSONArray(jsonText)
                val innerArray = outerArray.optJSONArray(0)
                if (innerArray != null) {
                    val sb = java.lang.StringBuilder()
                    for (i in 0 until innerArray.length()) {
                        val part = innerArray.optJSONArray(i)
                        val translatedText = part?.optString(0)
                        if (!translatedText.isNullOrEmpty()) {
                            sb.append(translatedText)
                        }
                    }
                    return@withContext sb.toString().trim()
                }
            }
        } catch (e: Exception) {
            Log.e("OMDB", "Translation to EN error for: $text", e)
        }
        ""
    }

    suspend fun translateToTurkish(text: String): String = withContext(Dispatchers.IO) {
        if (text.isEmpty() || text.equals("N/A", true)) return@withContext ""
        try {
            val urlString = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=tr&dt=t&q=${URLEncoder.encode(text, "UTF-8")}"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                val outerArray = JSONArray(jsonText)
                val innerArray = outerArray.optJSONArray(0)
                if (innerArray != null) {
                    val sb = java.lang.StringBuilder()
                    for (i in 0 until innerArray.length()) {
                        val part = innerArray.optJSONArray(i)
                        val translatedText = part?.optString(0)
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

    suspend fun fetchShowDetails(showName: String): ShowDetailsEntity? = withContext(Dispatchers.IO) {
        val queryName = mapShowNameForQuery(showName)
        var jsonObject: JSONObject? = null
        
        // Match 1: Search by title exactly (`t=queryName`)
        var url1 = "https://www.omdbapi.com/?apikey=$API_KEY&t=${URLEncoder.encode(queryName, "UTF-8")}"
        jsonObject = executeQuery(url1)
        
        // Match 2: If fail, translate name to English and search exactly (`t=engQuery`)
        var engQuery = ""
        if (jsonObject == null) {
            engQuery = translateToEnglish(queryName)
            if (engQuery.isNotEmpty() && !engQuery.equals(queryName, true)) {
                val url2 = "https://www.omdbapi.com/?apikey=$API_KEY&t=${URLEncoder.encode(engQuery, "UTF-8")}"
                jsonObject = executeQuery(url2)
            }
        }
        
        // Match 3: If fail, query list for queryName and fetch details of first item (`s=queryName`)
        if (jsonObject == null) {
            val url3 = "https://www.omdbapi.com/?apikey=$API_KEY&s=${URLEncoder.encode(queryName, "UTF-8")}"
            val searchObj = executeQuery(url3)
            val searchList = searchObj?.optJSONArray("Search")
            val firstItem = searchList?.optJSONObject(0)
            val imdbId = firstItem?.optString("imdbID", "")
            if (!imdbId.isNullOrEmpty()) {
                val urlId = "https://www.omdbapi.com/?apikey=$API_KEY&i=$imdbId"
                jsonObject = executeQuery(urlId)
            }
        }
        
        // Match 4: If fail, query list for engQuery and fetch details of first item (`s=engQuery`)
        if (jsonObject == null && engQuery.isNotEmpty()) {
            val url4 = "https://www.omdbapi.com/?apikey=$API_KEY&s=${URLEncoder.encode(engQuery, "UTF-8")}"
            val searchObj = executeQuery(url4)
            val searchList = searchObj?.optJSONArray("Search")
            val firstItem = searchList?.optJSONObject(0)
            val imdbId = firstItem?.optString("imdbID", "")
            if (!imdbId.isNullOrEmpty()) {
                val urlId = "https://www.omdbapi.com/?apikey=$API_KEY&i=$imdbId"
                jsonObject = executeQuery(urlId)
            }
        }
        
        if (jsonObject != null) {
            val title = jsonObject.optString("Title", showName)
            val poster = jsonObject.optString("Poster", "")
            val englishPlot = jsonObject.optString("Plot", "")
            val rating = jsonObject.optString("imdbRating", "")
            val genre = jsonObject.optString("Genre", "")
            val year = jsonObject.optString("Year", "")
            val actors = jsonObject.optString("Actors", "")
            
            // Translate description and genres to Turkish
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
            
            val finalPoster = if (poster.startsWith("http")) poster else ""

            return@withContext ShowDetailsEntity(
                showName = showName,
                posterUrl = finalPoster,
                plot = translatedPlot,
                rating = rating,
                genre = translatedGenre,
                year = year,
                actors = actors
            )
        }
        
        null
    }
}
