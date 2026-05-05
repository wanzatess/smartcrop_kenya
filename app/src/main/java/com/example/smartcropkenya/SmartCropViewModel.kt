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
    data class Success(val result: CropPredictionResult) : UiState()
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

    private val _recommendationState = MutableStateFlow<RecommendationUiState>(RecommendationUiState.Idle)
    val recommendationState = _recommendationState.asStateFlow()

    private var lastLocation: SubcountyLocation? = null
    private var lastMetrics: SoilMetrics? = null

    init {
        viewModelScope.launch {
            _locations.value = repository.getLocations()
            // Temporary - remove after testing
            val models = cropRecommendationRepository.listModels()
            android.util.Log.d("GeminiModels", models)
        }
    }

    fun submitData(location: SubcountyLocation, n: Int, p: Int, k: Int, ph: Double) {
        val metrics = SoilMetrics(n, p, k, ph)
        lastLocation = location
        lastMetrics = metrics

        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getCropPrediction(location, metrics)
            result.onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Failed") }

            _weatherState.value = WeatherUiState.Loading
            val weather = weatherRepository.getThreeMonthForecast(location.lat, location.lon)
            weather.onSuccess {
                _weatherState.value = WeatherUiState.Success(it)
                fetchRecommendation(location, metrics, it)
            }.onFailure {
                _weatherState.value = WeatherUiState.Error(it.message ?: "Weather fetch failed")
            }
        }
    }

    fun refreshRecommendation() {
        val location = lastLocation ?: return
        val metrics = lastMetrics ?: return
        val weather = (_weatherState.value as? WeatherUiState.Success)?.summaries ?: return
        viewModelScope.launch { fetchRecommendation(location, metrics, weather) }
    }

    private suspend fun fetchRecommendation(
        location: SubcountyLocation,
        metrics: SoilMetrics,
        weather: List<MonthlyWeatherSummary>
    ) {
        _recommendationState.value = RecommendationUiState.Loading
        val rec = cropRecommendationRepository.getRecommendation(location, metrics, weather)
        rec.onSuccess { _recommendationState.value = RecommendationUiState.Success(it) }
            .onFailure { _recommendationState.value = RecommendationUiState.Error(it.message ?: "Failed") }
    }

    fun resetToInput() {
        _uiState.value = UiState.Input
        _weatherState.value = WeatherUiState.Idle
        _recommendationState.value = RecommendationUiState.Idle
    }
}