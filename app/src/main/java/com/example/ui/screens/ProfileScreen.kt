package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var isChildLockEnabled by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    
    val currentPin = "1234"

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profil Yönetimi", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Çocuk Kilidi", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = isChildLockEnabled,
                    onCheckedChange = { 
                        if (it) {
                            showPinDialog = true
                        } else {
                            isChildLockEnabled = false
                        }
                    }
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Button(onClick = { /* Handle offline downloads navigate */ }) {
            Text("İndirilenler")
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Çocuk Kilidi Pin Kodu (1234)") },
            text = {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { pinInput = it },
                    visualTransformation = PasswordVisualTransformation()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput == currentPin) {
                            isChildLockEnabled = true
                            showPinDialog = false
                            pinInput = ""
                        }
                    }
                ) {
                    Text("Onayla")
                }
            }
        )
    }
}
