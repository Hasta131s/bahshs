package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppContainer
import com.example.ui.screens.MoonToonApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup Uncaught Exception Handler to capture any crash
        val prefs = getSharedPreferences("crash_prefs", Context.MODE_PRIVATE)
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val stackTrace = android.util.Log.getStackTraceString(throwable)
            prefs.edit().putString("last_crash", stackTrace).commit()
            defaultHandler?.uncaughtException(thread, throwable)
        }
        
        val cachedCrash = prefs.getString("last_crash", null)
        appContainer = AppContainer(this)
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                var crashText by remember { mutableStateOf(cachedCrash) }
                
                if (crashText != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0E1119))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "MoonToon Kurtarma Sistemi",
                                color = Color(0xFFFFC633),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Text(
                                text = "Uygulama beklenmedik şekilde sonlandı. Ayarları veya film listesini sıfırlayarak kurtarmayı deneyebilirsiniz.",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp)
                                    .padding(bottom = 24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2134))
                            ) {
                                Box(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                                    Text(
                                        text = crashText!!,
                                        color = Color(0xFF00BCFF),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        prefs.edit().remove("last_crash").commit()
                                        deleteDatabase("media_db")
                                        crashText = null
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCFF), contentColor = Color.Black)
                                ) {
                                    Text("Uygulamayı Sıfırla ve Onar", fontWeight = FontWeight.Bold)
                                }
                                
                                TextButton(
                                    onClick = {
                                        prefs.edit().remove("last_crash").commit()
                                        crashText = null
                                    }
                                ) {
                                    Text("Yoksay", color = Color.Gray)
                                }
                            }
                        }
                    }
                } else {
                    MoonToonApp(appContainer)
                }
            }
        }
    }
}
