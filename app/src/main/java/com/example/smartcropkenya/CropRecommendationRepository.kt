package com.example.smartcropkenya

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

// ── Request / Response data classes ──────────────────────────────────────────
data class LocationRequest(
    val latitude: Double,
    val longitude: Double
)

data class CropRecommendation(
    val crop: String,
    val confidence: Double
)

data class LocationPredictionResponse(
    val recommendations: List<CropRecommendation>,
    val detected_conditions: DetectedConditions?
)

// ── Repository ────────────────────────────────────────────────────────────────
class CropRecommendationRepository {

    private val baseUrl = "https://nduaek-crop-recommendation.hf.space"
    private val tag = "CropRepo"

    suspend fun getRecommendationsByLocation(
        latitude: Double,
        longitude: Double
    ): Result<LocationPredictionResponse> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/predict_by_location")
            Log.d(tag, "POST $url with lat=$latitude lon=$longitude")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 30_000
            connection.readTimeout = 60_000 // Hugging Face cold starts can be slow

            val body = """{"latitude":$latitude,"longitude":$longitude}"""
            Log.d(tag, "Request body: $body")
            OutputStreamWriter(connection.outputStream).use { it.write(body) }

            val responseCode = connection.responseCode
            Log.d(tag, "Response code: $responseCode")

            // Read body whether success or error
            val responseText = try {
                connection.inputStream.bufferedReader().readText()
            } catch (e: Exception) {
                // On error codes, body is in errorStream
                connection.errorStream?.bufferedReader()?.readText() ?: "No error body"
            }
            Log.e(tag, "FULL SERVER RESPONSE: $responseText")

            if (responseCode != 200) {
                return@withContext Result.failure(
                    Exception("Server error $responseCode: $responseText")
                )
            }

            val json = JSONObject(responseText)

            // Parse recommendations
            val recommendationsArray = json.getJSONArray("recommendations")
            val recommendations = (0 until recommendationsArray.length()).map { i ->
                val item = recommendationsArray.getJSONObject(i)
                CropRecommendation(
                    crop = item.getString("crop"),
                    confidence = item.getDouble("confidence")
                )
            }

            // Parse detected conditions (optional)
            val conditions: DetectedConditions? = if (json.has("detected_conditions")) {
                try {
                    val dc = json.getJSONObject("detected_conditions")
                    val soil = dc.getJSONObject("soil")
                    val weather = dc.getJSONObject("weather")
                    DetectedConditions(
                        soil = SoilConditions(
                            N_ppm = soil.getDouble("N"),
                            P_ppm = soil.getDouble("P"),
                            K_ppm = soil.getDouble("K"),
                            pH = soil.getDouble("ph")
                        ),
                        weather = WeatherConditions(
                            avg_temperature_c = weather.getDouble("temp"),
                            avg_humidity_pct = weather.getDouble("hum"),
                            avg_monthly_rainfall_mm = weather.getDouble("rain")
                        )
                    )
                } catch (e: Exception) {
                    Log.w(tag, "Could not parse detected_conditions: ${e.message}")
                    null
                }
            } else null

            Log.d(tag, "Parsed ${recommendations.size} recommendations")
            Result.success(
                LocationPredictionResponse(
                    recommendations = recommendations,
                    detected_conditions = conditions
                )
            )

        } catch (e: Exception) {
            Log.e(tag, "Request failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}