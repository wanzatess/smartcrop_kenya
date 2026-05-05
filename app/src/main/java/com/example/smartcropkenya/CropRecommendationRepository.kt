package com.example.smartcropkenya

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CropRecommendationRepository {

    private val apiKey = "AIzaSyB3BhiaWLgGzN0KU8xOAr1E-hFh_exP030"

    suspend fun listModels(): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            response
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: "Unknown error"
        }
    }

    suspend fun getRecommendation(
        location: SubcountyLocation,
        metrics: SoilMetrics,
        weatherSummaries: List<MonthlyWeatherSummary>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val weatherText = weatherSummaries.joinToString("\n") { month ->
                "- ${month.monthName}: Max ${"%.1f".format(month.avgMaxTemp)}°C, " +
                        "Min ${"%.1f".format(month.avgMinTemp)}°C, " +
                        "Rainfall ${"%.0f".format(month.totalRainfall)}mm, " +
                        "Humidity ${"%.0f".format(month.avgHumidity)}%"
            }

            val prompt = """
                You are an expert agricultural advisor for Kenya.
                
                A farmer in ${location.name}, ${location.county} County has the following soil and weather data:
                
                Soil Nutrients:
                - Nitrogen (N): ${metrics.n} mg/kg
                - Phosphorus (P): ${metrics.p} mg/kg
                - Potassium (K): ${metrics.k} mg/kg
                - Soil pH: ${metrics.ph}
                
                3-Month Weather Forecast:
                $weatherText
                
                Based on this data, recommend the top 3 most suitable crops for this farmer to grow.
                For each crop, briefly explain why it suits the soil and weather conditions.
                Keep the response concise and practical for a small-scale Kenyan farmer.
            """.trimIndent()

            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val body = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            OutputStreamWriter(connection.outputStream).use { it.write(body.toString()) }

            val responseCode = connection.responseCode
            val response = if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream.bufferedReader().use { it.readText() }
                return@withContext Result.failure(Exception("API error: $responseCode"))
            }

            val root = JSONObject(response)
            val text = root
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}