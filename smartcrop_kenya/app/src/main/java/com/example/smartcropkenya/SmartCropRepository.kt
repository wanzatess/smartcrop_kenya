// Save in SmartCropRepository.kt (or a new file DataModels.kt)
package com.example.smartcropkenya

data class SubcountyLocation(val name: String, val lat: Double, val lon: Double, val county: String)
data class SoilMetrics(val n: Int, val p: Int, val k: Int, val ph: Double)
data class CropPredictionResult(val topCrops: List<String>, val averageTemp: Double, val averageRainfall: Double)

interface SmartCropRepository {
    suspend fun getLocations(): List<SubcountyLocation>
    suspend fun getCropPrediction(location: SubcountyLocation, metrics: SoilMetrics): Result<CropPredictionResult>
}