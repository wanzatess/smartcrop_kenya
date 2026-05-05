package com.example.smartcropkenya

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RealSmartCropRepository(private val context: Context) : SmartCropRepository {

    override suspend fun getLocations(): List<SubcountyLocation> = withContext(Dispatchers.IO) {
        val json = context.assets.open("locations_dict.json")
            .bufferedReader()
            .use { it.readText() }

        val locations = mutableListOf<SubcountyLocation>()

        try {
            val root = JSONObject(json)
            val keys = root.keys()

            while (keys.hasNext()) {
                val subcountyName = keys.next()
                val entry = root.getJSONObject(subcountyName)

                locations.add(
                    SubcountyLocation(
                        name = subcountyName,
                        lat = entry.optDouble("lat", 0.0),
                        lon = entry.optDouble("lon", 0.0),
                        county = entry.optString("county", "Unknown")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        locations.sortedWith(compareBy({ it.county }, { it.name }))
    }

    override suspend fun getCropPrediction(
        location: SubcountyLocation,
        metrics: SoilMetrics
    ): Result<CropPredictionResult> = withContext(Dispatchers.IO) {
        try {
            val mockResult = CropPredictionResult(
                topCrops = listOf("Maize", "Beans", "Wheat"),
                averageTemp = 22.5,
                averageRainfall = 800.0
            )
            Result.success(mockResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}