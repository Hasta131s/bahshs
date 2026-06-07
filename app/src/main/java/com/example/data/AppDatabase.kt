package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey val id: String,
    val showName: String,
    val title: String,
    val streamUrl: String,
    val logoUrl: String,
    val category: String,
    var isFavorite: Boolean = false,
    var watchProgress: Long = 0,
    var totalDuration: Long = 0,
    var lastWatchedTime: Long = 0
)

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items")
    suspend fun getAll(): List<MediaEntity>

    @Query("SELECT * FROM media_items")
    fun getAllFlow(): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mediaList: List<MediaEntity>)
    
    @Update
    suspend fun update(media: MediaEntity)

    @Query("SELECT * FROM media_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): MediaEntity?
    
    @Query("SELECT * FROM media_items WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<MediaEntity>>
    
    @Query("SELECT * FROM media_items WHERE lastWatchedTime > 0 ORDER BY lastWatchedTime DESC")
    fun getHistory(): Flow<List<MediaEntity>>
}

@Database(entities = [MediaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
}

