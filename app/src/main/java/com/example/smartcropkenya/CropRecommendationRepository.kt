package com.example.smartcropkenya

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// --- Data classes ---

data class CropInput(
    val N: Float,
    val P: Float,
    val K: Float,
    val temperature: Float,
    val humidity: Float,
    val ph: Float,
    val rainfall: Float
)

data class CropResult(
    val crop: String,
    val confidence: Float
)

data class CropResponse(
    val recommendations: List<CropResult>
)

// --- Retrofit API interface ---

interface CropApiService {
    @POST("predict")
    suspend fun predict(@Body input: CropInput): CropResponse
}

// --- Repository ---

class CropRecommendationRepository {

    private val api: CropApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://nduaek-crop-recommendation.hf.space/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CropApiService::class.java)
    }

    suspend fun getRecommendations(
        n: Float,
        p: Float,
        k: Float,
        temperature: Float,
        humidity: Float,
        ph: Float,
        rainfall: Float
    ): Result<List<CropResult>> {
        return try {
            val input = CropInput(n, p, k, temperature, humidity, ph, rainfall)
            val response = api.predict(input)
            Result.success(response.recommendations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}