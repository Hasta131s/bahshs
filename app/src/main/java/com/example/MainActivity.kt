package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.theme.MyApplicationTheme
import com.example.data.AppContainer
import com.example.data.M3uParser
import com.example.ui.screens.MoonToonApp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.MediaRepository
import com.example.ui.screens.MainViewModel
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.worker.NotificationWorker
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        appContainer = AppContainer(applicationContext)
        
        CoroutineScope(Dispatchers.IO).launch {
            M3uParser(applicationContext).parseAndSeedDatabase(appContainer.database)
        }

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(8, TimeUnit.HOURS).build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val repo = MediaRepository(
                                appContainer.database.mediaDao(),
                                appContainer.omdbApi
                            )
                            return MainViewModel(repo) as T
                        }
                    }
                )
                MoonToonApp(viewModel = viewModel, appContainer = appContainer)
            }
        }
    }
}
