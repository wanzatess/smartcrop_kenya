package com.example.smartcropkenya

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Yard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Shows the soil + weather conditions that the backend detected for the
 * selected location. Replaces the old WeatherForecastSection.
 *
 * Usage in ResultsScreen (inside UiState.Success branch):
 *   state.conditions?.let { DetectedConditionsSection(it) }
 */
@Composable
fun DetectedConditionsSection(conditions: DetectedConditions) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Why this recommendation?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "These are the local conditions detected for your area:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // ── Weather card ──────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "3-Month Weather Outlook",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherStat(
                        icon = Icons.Outlined.Thermostat,
                        label = "Avg Temp",
                        value = "${"%.1f".format(conditions.weather.avg_temperature_c)}°C"
                    )
                    WeatherStat(
                        icon = Icons.Outlined.WaterDrop,
                        label = "Rain/month",
                        value = "${"%.0f".format(conditions.weather.avg_monthly_rainfall_mm)} mm"
                    )
                    WeatherStat(
                        icon = Icons.Outlined.Cloud,
                        label = "Humidity",
                        value = "${"%.0f".format(conditions.weather.avg_humidity_pct)}%"
                    )
                }
            }
        }

        // ── Soil card ─────────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Detected Soil Profile (0–20 cm)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherStat(
                        icon = Icons.Outlined.Yard,
                        label = "Nitrogen",
                        value = "${"%.1f".format(conditions.soil.N_ppm)} ppm"
                    )
                    WeatherStat(
                        icon = Icons.Outlined.Yard,
                        label = "Phosphorus",
                        value = "${"%.1f".format(conditions.soil.P_ppm)} ppm"
                    )
                    WeatherStat(
                        icon = Icons.Outlined.Yard,
                        label = "Potassium",
                        value = "${"%.1f".format(conditions.soil.K_ppm)} ppm"
                    )
                    WeatherStat(
                        icon = Icons.Outlined.Yard,
                        label = "pH",
                        value = "${"%.1f".format(conditions.soil.pH)}"
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherStat(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}