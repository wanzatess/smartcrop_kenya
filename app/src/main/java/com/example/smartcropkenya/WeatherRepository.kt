package com.example.smartcropkenya

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

class WeatherRepository {

    suspend fun getThreeMonthForecast(lat: Double, lon: Double): Result<List<MonthlyWeatherSummary>> =
        withContext(Dispatchers.IO) {
            try {
                val today = LocalDate.now()
                val startDate = today.withDayOfMonth(1)
                val endDate = today.plusMonths(3).withDayOfMonth(1).minusDays(1)

                val url = "https://climate-api.open-meteo.com/v1/climate?" +
                        "latitude=$lat&longitude=$lon" +
                        "&start_date=$startDate&end_date=$endDate" +
                        "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,relative_humidity_2m_mean" +
                        "&models=CMCC_CM2_VHR4"

                val response = URL(url).readText()
                val root = JSONObject(response)
                val daily = root.getJSONObject("daily")

                val dates = daily.getJSONArray("time")
                val maxTemps = daily.getJSONArray("temperature_2m_max")
                val minTemps = daily.getJSONArray("temperature_2m_min")
                val rainfall = daily.getJSONArray("precipitation_sum")
                val humidity = daily.getJSONArray("relative_humidity_2m_mean")

                // Group by month
                data class DayData(
                    val maxTemp: Double, val minTemp: Double,
                    val rain: Double, val humidity: Double
                )

                val monthMap = mutableMapOf<Month, MutableList<DayData>>()

                for (i in 0 until dates.length()) {
                    val date = LocalDate.parse(dates.getString(i))
                    val dayData = DayData(
                        maxTemp = if (maxTemps.isNull(i)) 0.0 else maxTemps.getDouble(i),
                        minTemp = if (minTemps.isNull(i)) 0.0 else minTemps.getDouble(i),
                        rain = if (rainfall.isNull(i)) 0.0 else rainfall.getDouble(i),
                        humidity = if (humidity.isNull(i)) 0.0 else humidity.getDouble(i)
                    )
                    monthMap.getOrPut(date.month) { mutableListOf() }.add(dayData)
                }

                val summaries = monthMap.entries
                    .sortedBy { it.key }
                    .map { (month, days) ->
                        MonthlyWeatherSummary(
                            monthName = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                            avgMaxTemp = days.map { it.maxTemp }.average(),
                            avgMinTemp = days.map { it.minTemp }.average(),
                            totalRainfall = days.sumOf { it.rain },
                            avgHumidity = days.map { it.humidity }.average()
                        )
                    }

                Result.success(summaries)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}