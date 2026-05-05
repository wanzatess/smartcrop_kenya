package com.example.smartcropkenya

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherForecastSection(weatherState: WeatherUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "3-Month Weather Forecast",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        when (weatherState) {
            is WeatherUiState.Idle -> {}
            is WeatherUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WeatherUiState.Error -> {
                Text(
                    "Could not load weather: ${weatherState.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            is WeatherUiState.Success -> {
                weatherState.summaries.forEach { month ->
                    MonthWeatherCard(month)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun MonthWeatherCard(summary: MonthlyWeatherSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                summary.monthName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherStat("🌡 Max", "${"%.1f".format(summary.avgMaxTemp)}°C")
                WeatherStat("🌡 Min", "${"%.1f".format(summary.avgMinTemp)}°C")
                WeatherStat("🌧 Rain", "${"%.0f".format(summary.totalRainfall)}mm")
                WeatherStat("💧 Humidity", "${"%.0f".format(summary.avgHumidity)}%")
            }
        }
    }
}

@Composable
fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}