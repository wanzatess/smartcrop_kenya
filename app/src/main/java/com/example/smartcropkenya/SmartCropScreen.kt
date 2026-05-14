package com.example.smartcropkenya

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SmartCropApp(
    viewModel: SmartCropViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToResults: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val locations by viewModel.locations.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (val state = uiState) {
            is UiState.Input -> InputFormScreen(
                locations = locations,
                onSubmit = { location ->
                    viewModel.predictByLocation(location)
                    onNavigateToResults() }
            )
            is UiState.Loading -> LoadingScreen()
            is UiState.Success -> {
                // Navigation to ResultsScreen is handled via onNavigateToResults callback.
                // This branch is a fallback in case navigation hasn't fired yet.
                LoadingScreen()
            }
            is UiState.Error -> ErrorScreen(state.message, viewModel::resetToInput)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFormScreen(
    locations: List<SubcountyLocation>,
    onSubmit: (SubcountyLocation) -> Unit
) {
    var selectedLocation by remember { mutableStateOf<SubcountyLocation?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SmartCrop Kenya", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Select your location and let the system detect soil & weather conditions automatically.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Select Location",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Show all locations as selectable chips/buttons
        locations.chunked(3).forEach { rowLocations ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowLocations.forEach { loc ->
                    val isSelected = selectedLocation == loc
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLocation = loc },
                        label = { Text(loc.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if row is not full
                repeat(3 - rowLocations.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (selectedLocation != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Selected: ${selectedLocation!!.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = selectedLocation != null,
            onClick = { onSubmit(selectedLocation!!) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Analyse & Predict Crops")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator()
            Text(
                "Analysing soil & weather data...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Error: $message", color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}