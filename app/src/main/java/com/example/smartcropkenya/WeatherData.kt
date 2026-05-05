package com.example.smartcropkenya

data class MonthlyWeatherSummary(
    val monthName: String,
    val avgMinTemp: Double,
    val avgMaxTemp: Double,
    val totalRainfall: Double,
    val avgHumidity: Double
)
sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val summaries: List<MonthlyWeatherSummary>) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
sealed class RecommendationUiState {
    object Idle : RecommendationUiState()
    object Loading : RecommendationUiState()
    data class Success(val text: String) : RecommendationUiState()
    data class Error(val message: String) : RecommendationUiState()
}