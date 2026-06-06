package com.example.data

import android.content.Context
import java.io.InputStreamReader
import java.util.UUID

class M3uParser(private val context: Context) {

    suspend fun parseAndSeedDatabase(database: AppDatabase) {
        val mediaDao = database.mediaDao()
        
        // Check if already seeded
        val allShows = mediaDao.getAllMedia() // Might throw if queried without Coroutines but we are inside suspend
        // Actually, just a simple check is needed, we assume we do this in worker or so
        
        try {
            val assets = context.assets
            val files = assets.list("m3u") ?: return
            
            val allMedia = mutableListOf<MediaEntity>()
            
            for (file in files) {
                val inputStream = assets.open("m3u/$file")
                val reader = InputStreamReader(inputStream)
                val lines = reader.readLines()
                reader.close()

                var currentTitle = ""
                var currentShowName = ""
                var currentLogo = ""
                var currentStreamUrl = ""
                
                for (line in lines) {
                    if (line.startsWith("#EXTINF")) {
                        // Example: #EXTINF:1 tvg-logo="http...",Adventure Time - Bölüm 1
                        val logoMatch = Regex("""tvg-logo="(.*?)"""").find(line)
                        if (logoMatch != null) {
                            currentLogo = logoMatch.groupValues[1]
                        }
                        
                        val commaParts = line.split(",")
                        if (commaParts.size > 1) {
                            val fullTitle = commaParts[1]
                            val titleParts = fullTitle.split(" - ")
                            if (titleParts.size >= 2) {
                                currentShowName = titleParts[0].trim()
                                currentTitle = titleParts[1].trim()
                            } else {
                                currentShowName = fullTitle.trim()
                                currentTitle = fullTitle.trim()
                            }
                        }
                    } else if (line.startsWith("http")) {
                        currentStreamUrl = line.trim()
                        
                        // Default category based on show
                        val category = when {
                            currentShowName.contains("Adventure Time", ignoreCase=true) -> "#macera #fantastik"
                            currentShowName.contains("Gumball", ignoreCase=true) -> "#komedi #macera"
                            currentShowName.contains("Kral Şakir", ignoreCase=true) -> "#komedi #aile"
                            currentShowName.contains("Ben 10", ignoreCase=true) -> "#aksiyon #macera"
                            currentShowName.contains("Powerpuff", ignoreCase=true) -> "#aksiyon #kahraman"
                            else -> "#çizgidizi #eğlence"
                        }
                        
                        val entity = MediaEntity(
                            id = UUID.randomUUID().toString(),
                            showName = currentShowName,
                            title = currentTitle,
                            logoUrl = currentLogo,
                            streamUrl = currentStreamUrl,
                            category = category,
                            tags = category,
                            isFavorite = false,
                            watchProgress = 0,
                            totalDuration = 0,
                            lastWatchedTime = 0,
                            isDownloaded = false,
                            localFileUri = null
                        )
                        allMedia.add(entity)
                        
                        // Reset
                        currentTitle = ""
                        currentShowName = ""
                        currentLogo = ""
                        currentStreamUrl = ""
                    }
                }
            }
            if (allMedia.isNotEmpty()) {
                mediaDao.insertAll(allMedia)
            }
            
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}
