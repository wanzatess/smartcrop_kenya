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
fun SmartCropApp(viewModel: SmartCropViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val locations by viewModel.locations.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (val state = uiState) {
            is UiState.Input -> InputFormScreen(locations, viewModel::submitData)
            is UiState.Loading -> LoadingScreen()
            is UiState.Success -> ResultScreen(state.result, viewModel::resetToInput)
            is UiState.Error -> ErrorScreen(state.message, viewModel::resetToInput)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFormScreen(
    locations: List<SubcountyLocation>,
    onSubmit: (SubcountyLocation, Int, Int, Int, Double) -> Unit
) {
    var selectedLocation by remember { mutableStateOf<SubcountyLocation?>(null) }
    var n by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var k by remember { mutableStateOf("") }
    var ph by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Soil Metrics", style = MaterialTheme.typography.headlineMedium)

        // Simple selection (In a real app, use a DropdownMenu)
        Text("Select Location: ${selectedLocation?.name ?: "None"}")
        Row {
            locations.take(3).forEach { loc ->
                Button(onClick = { selectedLocation = loc }, modifier = Modifier.padding(4.dp)) {
                    Text(loc.name)
                }
            }
        }

        OutlinedTextField(value = n, onValueChange = { n = it }, label = { Text("Nitrogen (N)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = p, onValueChange = { p = it }, label = { Text("Phosphorus (P)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = k, onValueChange = { k = it }, label = { Text("Potassium (K)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = ph, onValueChange = { ph = it }, label = { Text("pH Level") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = selectedLocation != null,
            onClick = {
                onSubmit(selectedLocation!!, n.toIntOrNull() ?: 0, p.toIntOrNull() ?: 0, k.toIntOrNull() ?: 0, ph.toDoubleOrNull() ?: 7.0)
            }
        ) {
            Text("Predict Crops")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Error: $message", color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun ResultScreen(result: CropPredictionResult, onReset: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Results", style = MaterialTheme.typography.titleLarge)
        Text("Temp: ${result.averageTemp}°C | Rain: ${result.averageRainfall}mm")
        result.topCrops.forEach { Text(it, style = MaterialTheme.typography.headlineSmall) }
        Button(onClick = onReset) { Text("Back") }
    }
}