package com.example.smartcropkenya

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Input : UiState()
    object Loading : UiState()
    data class Success(val recommendations: List<CropResult>) : UiState()
    data class Error(val message: String) : UiState()
}

class SmartCropViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SmartCropRepository = RealSmartCropRepository(application)
    private val weatherRepository = WeatherRepository()
    private val cropRecommendationRepository = CropRecommendationRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Input)
    val uiState = _uiState.asStateFlow()

    private val _locations = MutableStateFlow<List<SubcountyLocation>>(emptyList())
    val locations = _locations.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState = _weatherState.asStateFlow()

    init {
        viewModelScope.launch {
            _locations.value = repository.getLocations()
        }
    }

    fun submitData(location: SubcountyLocation, n: Int, p: Int, k: Int, ph: Double) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            // Fetch weather
            val weatherResult = weatherRepository.getThreeMonthForecast(location.lat, location.lon)

            // Update weather state so the UI can display it
            if (weatherResult.isSuccess) {
                val summaries = weatherResult.getOrDefault(emptyList())
                _weatherState.value = WeatherUiState.Success(summaries)
            } else {
                _weatherState.value = WeatherUiState.Error(weatherResult.exceptionOrNull()?.message ?: "Weather fetch failed")
            }

            // Average out weather values for the crop API
            val temperature: Float
            val humidity: Float
            val rainfall: Float

            if (weatherResult.isSuccess) {
                val summaries = weatherResult.getOrDefault(emptyList())
                temperature = summaries.map { (it.avgMaxTemp + it.avgMinTemp) / 2 }.average().toFloat()
                humidity = summaries.map { it.avgHumidity }.average().toFloat()
                rainfall = summaries.sumOf { it.totalRainfall }.toFloat()
            } else {
                temperature = 24f
                humidity = 70f
                rainfall = 150f
            }

            // Call crop recommendation API
            val result = cropRecommendationRepository.getRecommendations(
                n = n.toFloat(),
                p = p.toFloat(),
                k = k.toFloat(),
                temperature = temperature,
                humidity = humidity,
                ph = ph.toFloat(),
                rainfall = rainfall
            )

            result.onSuccess { crops ->
                _uiState.value = UiState.Success(crops)
            }
            result.onFailure { error ->
                _uiState.value = UiState.Error(error.message ?: "Something went wrong")
            }
        }
    }

    fun resetToInput() {
        _uiState.value = UiState.Input
        _weatherState.value = WeatherUiState.Idle
    }
}