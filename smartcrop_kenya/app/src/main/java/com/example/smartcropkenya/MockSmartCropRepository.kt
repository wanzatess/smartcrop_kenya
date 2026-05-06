package com.example.smartcropkenya
import kotlinx.coroutines.delay

class MockSmartCropRepository : SmartCropRepository {
    override suspend fun getLocations(): List<SubcountyLocation> {
        return listOf(
            SubcountyLocation("Westlands", -1.26, 36.80, "Nairobi"),
            SubcountyLocation("Alego Usonga", 0.06, 34.24, "Siaya"),
            SubcountyLocation("Kieni", -0.32, 36.93, "Nyeri")
        )
    }

    override suspend fun getCropPrediction(location: SubcountyLocation, metrics: SoilMetrics): Result<CropPredictionResult> {
        delay(1500)
        return Result.success(CropPredictionResult(listOf("Maize", "Beans"), 24.5, 120.0))
    }
}