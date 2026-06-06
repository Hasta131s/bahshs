package com.example.data

import kotlinx.coroutines.flow.Flow

class MediaRepository(private val mediaDao: MediaDao, private val omdbApi: OmdbApi) {

    fun getAllMedia(): Flow<List<MediaEntity>> = mediaDao.getAllMedia()

    fun getFavorites(): Flow<List<MediaEntity>> = mediaDao.getFavorites()

    fun getWatchHistory(): Flow<List<MediaEntity>> = mediaDao.getWatchHistory()

    fun getDownloads(): Flow<List<MediaEntity>> = mediaDao.getDownloads()

    fun getAllShows(): Flow<List<ShowInfo>> = mediaDao.getAllShows()

    fun getMediaByShow(showName: String): Flow<List<MediaEntity>> = mediaDao.getMediaByShow(showName)

    suspend fun getMediaById(id: String): MediaEntity? = mediaDao.getMediaById(id)

    suspend fun setFavorite(id: String, isFavorite: Boolean) {
        mediaDao.setFavorite(id, isFavorite)
    }

    suspend fun updateWatchProgress(id: String, progress: Long, total: Long) {
        mediaDao.getMediaById(id)?.let {
            mediaDao.update(it.copy(
                watchProgress = progress, 
                totalDuration = total, 
                lastWatchedTime = System.currentTimeMillis()
            ))
        }
    }
    
    suspend fun markAsDownloaded(id: String, localPath: String) {
        mediaDao.getMediaById(id)?.let {
            mediaDao.update(it.copy(isDownloaded = true, localFileUri = localPath))
        }
    }

    suspend fun getShowDetails(title: String): OmdbResponse? {
        return try {
            val response = omdbApi.getShowDetails(title = title)
            if (response.Response == "True") response else null
        } catch (e: Exception) {
            null
        }
    }
}
