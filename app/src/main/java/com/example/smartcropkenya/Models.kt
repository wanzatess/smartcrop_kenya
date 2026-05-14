package com.example.smartcropkenya

// ── Location ──────────────────────────────────────────────────────────────────
data class SubcountyLocation(
    val name: String,
    val lat: Double,
    val lon: Double,
    val county: String
)

// ── Crop result — used by ViewModel and ResultsScreen ─────────────────────────
data class CropResult(
    val crop: String,
    val confidence: Double
)

// ── Detected conditions — returned by backend, shown in ResultsScreen ─────────
data class SoilConditions(
    val N_ppm: Double,
    val P_ppm: Double,
    val K_ppm: Double,
    val pH: Double
)

data class WeatherConditions(
    val avg_temperature_c: Double,
    val avg_humidity_pct: Double,
    val avg_monthly_rainfall_mm: Double
)

data class DetectedConditions(
    val soil: SoilConditions,
    val weather: WeatherConditions
)

// ── Repository interface — only locations needed now ──────────────────────────
interface SmartCropRepository {
    suspend fun getLocations(): List<SubcountyLocation>
}