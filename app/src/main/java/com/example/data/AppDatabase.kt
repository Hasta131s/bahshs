package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "media_item")
data class MediaEntity(
    @PrimaryKey val id: String, // e.g. "adventure-time-s1e1"
    val showName: String,
    val title: String,
    val logoUrl: String,
    val streamUrl: String,
    val category: String, // e.g., "#animasyon #komedi"
    val tags: String, // Comma separated
    val isFavorite: Boolean = false,
    val watchProgress: Long = 0L,
    val totalDuration: Long = 0L,
    val lastWatchedTime: Long = 0L,
    val isDownloaded: Boolean = false,
    val localFileUri: String? = null
)

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_item")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_item WHERE id = :id")
    suspend fun getMediaById(id: String): MediaEntity?

    @Query("SELECT * FROM media_item WHERE showName = :showName")
    fun getMediaByShow(showName: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_item WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_item WHERE lastWatchedTime > 0 ORDER BY lastWatchedTime DESC")
    fun getWatchHistory(): Flow<List<MediaEntity>>

    @Query("SELECT DISTINCT showName, logoUrl, category FROM media_item GROUP BY showName")
    fun getAllShows(): Flow<List<ShowInfo>>
    
    @Query("SELECT * FROM media_item WHERE isDownloaded = 1")
    fun getDownloads(): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(media: List<MediaEntity>)
    
    @Update
    suspend fun update(media: MediaEntity)

    @Query("UPDATE media_item SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: String, isFav: Boolean)
}

data class ShowInfo(
    val showName: String,
    val logoUrl: String,
    val category: String
)

@Database(entities = [MediaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
}
