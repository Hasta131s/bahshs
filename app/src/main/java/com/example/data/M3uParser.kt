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
                    if (line.startsWith("#EXTINF:")) {
                        val logoMatch = Regex("tvg-logo=\"([^\"]+)\"").find(line)
                        currentLogo = logoMatch?.groupValues?.get(1) ?: ""

                        val groupMatch = Regex("group-title=\"([^\"]+)\"").find(line)
                        val groupTitle = groupMatch?.groupValues?.get(1)?.trim()

                        val titlePart = line.substringAfterLast(",").trim()

                        if (!groupTitle.isNullOrEmpty()) {
                            currentShow = groupTitle
                            currentTitle = titlePart
                        } else if (titlePart.contains(" - ")) {
                            currentShow = titlePart.substringBeforeLast(" - ").trim()
                            currentTitle = titlePart.substringAfterLast(" - ").trim()
                        } else if (titlePart.contains("-")) {
                            currentShow = titlePart.substringBefore("-").trim()
                            currentTitle = titlePart.substringAfter("-").trim()
                        } else {
                            val seasonMatch = Regex("(?i)(.*?)(\\s*(?:S\\d+|\\d+\\.Sezon|\\d+\\.Bölüm))").find(titlePart)
                            if (seasonMatch != null) {
                                currentShow = seasonMatch.groupValues[1].trim()
                                currentTitle = titlePart
                            } else {
                                currentShow = titlePart
                                currentTitle = titlePart
                            }
                        }
                        
                        // Fallbacks if extraction is generic
                        if (currentShow.isEmpty() || currentShow.equals("null", true)) {
                            currentShow = category
                        }
                    } else if (line.isNotEmpty() && !line.startsWith("#")) {
                        val url = line.trim()
                        if (url.startsWith("http")) { // filter valid URLs
                            val id = UUID.nameUUIDFromBytes(url.toByteArray()).toString()
                            mediaList.add(MediaEntity(id, currentShow, currentTitle, url, currentLogo, category))
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
