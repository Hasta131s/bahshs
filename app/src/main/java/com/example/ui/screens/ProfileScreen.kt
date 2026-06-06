package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.theme.RedMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var isChildLockEnabled by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    val currentPin = "1234"

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Profil", fontWeight = FontWeight.Bold, color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        
        Column(Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Çocuk Kilidi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = isChildLockEnabled,
                        onCheckedChange = { 
                            if (it) {
                                showPinDialog = true
                            } else {
                                isChildLockEnabled = false
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RedMain)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = { /* Handle offline downloads */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("İndirilenler", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = { /* Check updates */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Güncellemeleri Kontrol Et", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("PIN Girin (1234)") },
            text = {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { pinInput = it },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedMain)
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedMain)
                ) {
                    Text("Onayla")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false; pinInput = "" }) {
                    Text("İptal", color = Color.Gray)
                }
            }
        )
    }
}
