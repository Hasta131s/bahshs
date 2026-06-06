package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object M3uParser {
    suspend fun parseFromAssets(context: Context): List<MediaEntity> = withContext(Dispatchers.IO) {
        val mediaList = mutableListOf<MediaEntity>()
        val assets = context.assets

        fun processFile(path: String, category: String) {
            val lines = assets.open(path).bufferedReader().readLines()
            var currentLogo = ""
            var currentTitle = ""
            var currentShow = ""

            for (line in lines) {
                if (line.startsWith("#EXTINF:")) {
                    val logoMatch = Regex("tvg-logo=\"([^\"]+)\"").find(line)
                    currentLogo = logoMatch?.groupValues?.get(1) ?: ""

                    val titlePart = line.substringAfterLast(",")
                    if (titlePart.contains("-")) {
                        currentShow = titlePart.substringBefore("-").trim()
                        currentTitle = titlePart.substringAfter("-").trim()
                    } else {
                        currentShow = titlePart.trim()
                        currentTitle = titlePart.trim()
                    }
                } else if (line.isNotEmpty() && !line.startsWith("#")) {
                    val url = line.trim()
                    if (url.startsWith("http")) { // filter valid URLs
                        val id = UUID.nameUUIDFromBytes(url.toByteArray()).toString()
                        mediaList.add(MediaEntity(id, currentShow, currentTitle, url, currentLogo, category))
                    }
                }
            }
        }

        fun scanAssetsDir(dir: String, defaultCategory: String) {
            val list = assets.list(dir) ?: emptyArray()
            for (item in list) {
                val fullPath = if (dir.isEmpty()) item else "${dir}/${item}"
                if (item.endsWith(".m3u")) {
                    processFile(fullPath, defaultCategory)
                } else if (!item.contains(".")) {
                    scanAssetsDir(fullPath, item)
                }
            }
        }
        
        scanAssetsDir("m3u", "Diğer")
        mediaList
    }
}
