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
            try {
                val lines = assets.open(path).bufferedReader().readLines()
                var currentLogo = ""
                var currentTitle = ""
                var currentShow = ""

                var itemsAdded = 0

                for (line in lines) {
                    if (itemsAdded >= 100) break // Prevent OOM and DB freeze
                    
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
                            itemsAdded++
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun scanAssetsDir(dir: String, defaultCategory: String) {
            val list = assets.list(dir) ?: emptyArray()
            for (item in list) {
                val fullPath = if (dir.isEmpty()) item else "${dir}/${item}"
                if (item.endsWith(".m3u")) {
                    val cleanCategory = when {
                        defaultCategory.contains("cartoonnetwork") -> "Cartoon Network"
                        defaultCategory.contains("puhutv") -> "Puhu TV"
                        defaultCategory.contains("cnnturk") -> "Belgesel & Haber"
                        defaultCategory.contains("dmax") -> "DMAX Özel"
                        defaultCategory.contains("kanald") -> "Kanal D"
                        defaultCategory.contains("trt") -> "TRT Çocuk"
                        defaultCategory.contains("star") -> "Star TV"
                        else -> defaultCategory
                    }
                    processFile(fullPath, cleanCategory)
                } else if (!item.contains(".")) {
                    scanAssetsDir(fullPath, item)
                }
            }
        }
        
        scanAssetsDir("m3u", "Diğer")
        mediaList
    }
}
