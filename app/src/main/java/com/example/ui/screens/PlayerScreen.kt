package com.example.ui.screens

import android.app.Activity
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.data.AppContainer
import com.example.data.MediaEntity
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(episodeId: String, appContainer: AppContainer, navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()
    var episode by remember { mutableStateOf<MediaEntity?>(null) }
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(episodeId) {
        val ep = appContainer.database.mediaDao().getById(episodeId)
        episode = ep
        ep?.let {
            val mediaItem = MediaItem.fromUri(Uri.parse(it.streamUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.seekTo(it.watchProgress)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        activity?.let { act ->
            val window = act.window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                exoPlayer.pause()
            } else if (event == Lifecycle.Event.ON_RESUME) {
                exoPlayer.play()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            activity?.let { act ->
                val window = act.window
                WindowCompat.setDecorFitsSystemWindows(window, true)
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    show(WindowInsetsCompat.Type.systemBars())
                }
            }
            
            lifecycleOwner.lifecycle.removeObserver(observer)
            val currentPos = exoPlayer.currentPosition
            val totalDur = exoPlayer.duration.coerceAtLeast(0L)
            exoPlayer.release()
            
            coroutineScope.launch {
                episode?.let { 
                    appContainer.database.mediaDao().update(
                        it.copy(
                            lastWatchedTime = System.currentTimeMillis(),
                            watchProgress = currentPos,
                            totalDuration = totalDur
                        )
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (episode != null) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        keepScreenOn = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
