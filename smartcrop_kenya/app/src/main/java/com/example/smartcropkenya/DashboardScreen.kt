package com.example.smartcropkenya

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    userName: String,
    isNewUser: Boolean,
    onNavigateToInput: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Dynamic Greeting
        Text(
            text = if (isNewUser) "Welcome to SmartCrop, $userName! 🌱" else "Welcome Back, $userName! 🌾",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "What would you like to do today?",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // Card 1: New Recommendation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onNavigateToInput() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🚜", fontSize = 40.sp, modifier = Modifier.padding(end = 16.dp))
                Column {
                    Text("Get Crop Recommendation", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Enter soil data for a new prediction", fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: View History
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onNavigateToHistory() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📜", fontSize = 40.sp, modifier = Modifier.padding(end = 16.dp))
                Column {
                    Text("View Crop History", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("See past seasonal recommendations", fontSize = 14.sp)
                }
            }
        }
    }
}