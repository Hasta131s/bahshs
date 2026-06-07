package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class OmdbRepository(private val dao: OmdbDao) {
    suspend fun getShowInfo(title: String): OmdbEntity? {
        val cached = dao.getByTitle(title)
        if (cached != null) return cached

        return withContext(Dispatchers.IO) {
            try {
                // If it's a generic word like "TRT Çocuk", maybe we skip OMDB?
                // For now, let's just query everything as asked.
                val safeTitle = URLEncoder.encode(title, "UTF-8")
                val urlStr = "https://www.omdbapi.com/?t=$safeTitle&apikey=bbb54f5d"
                val responseStr = URL(urlStr).readText()
                val json = JSONObject(responseStr)
                if (json.optString("Response") == "True") {
                    var poster = json.optString("Poster")
                    if (poster == "N/A") poster = ""
                    val entity = OmdbEntity(
                        title = title,
                        poster = poster,
                        plot = json.optString("Plot", "Detay bulunamadı."),
                        genre = json.optString("Genre", ""),
                        actors = json.optString("Actors", ""),
                        imdbRating = json.optString("imdbRating", ""),
                        lastFetched = System.currentTimeMillis()
                    )
                    dao.insert(entity)
                    entity
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
