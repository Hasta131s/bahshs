package com.example.data

import android.content.Context
import androidx.room.Room

class AppContainer(private val context: Context) {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "media_db").build()
    }
}
