package com.example.smartcropkenya

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// A simple data class to hold our fake history records
data class HistoryRecord(
    val date: String,
    val location: String,
    val crop: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onNavigateBack: () -> Unit) {
    // Mock History Data
    val pastRecords = listOf(
        HistoryRecord("April 2026", "Alego Usonga, Siaya", "Sorghum", "🌾"),
        HistoryRecord("November 2025", "Westlands, Nairobi", "Beans", "🫘"),
        HistoryRecord("March 2025", "Kieni, Nyeri", "Maize", "🌽"),
        HistoryRecord("October 2024", "Alego Usonga, Siaya", "Cassava", "🥔")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prediction History", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // LazyColumn is used for scrolling lists
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pastRecords) { record ->
                    HistoryCard(record)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 16.dp)
            ) {
                Text("Back to Dashboard", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun HistoryCard(record: HistoryRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(record.emoji, fontSize = 40.sp, modifier = Modifier.padding(end = 16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(record.crop, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(record.location, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text(record.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}