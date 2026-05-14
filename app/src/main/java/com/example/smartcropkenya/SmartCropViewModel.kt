package com.example.smartcropkenya

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────
sealed class UiState {
    object Input : UiState()
    object Loading : UiState()
    data class Success(
        val recommendations: List<CropResult>,
        val conditions: DetectedConditions?
    ) : UiState()
    data class Error(val message: String) : UiState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
class SmartCropViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SmartCropRepository = RealSmartCropRepository(application)
    private val cropRecommendationRepository = CropRecommendationRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Input)
    val uiState = _uiState.asStateFlow()

    private val _locations = MutableStateFlow<List<SubcountyLocation>>(emptyList())
    val locations = _locations.asStateFlow()

    init {
        viewModelScope.launch {
            _locations.value = repository.getLocations()
        }
    }

    // No onSuccess callback — caller navigates immediately after calling this.
    // ResultsScreen observes uiState and handles Loading → Success → Error itself.
    fun predictByLocation(location: SubcountyLocation) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = cropRecommendationRepository.getRecommendationsByLocation(
                latitude = location.lat,
                longitude = location.lon
            )
            result.fold(
                onSuccess = { response ->
                    _uiState.value = UiState.Success(
                        recommendations = response.recommendations.map {
                            CropResult(crop = it.crop, confidence = it.confidence)
                        },
                        conditions = response.detected_conditions
                    )
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "Something went wrong. Please try again."
                    )
                }
            )
        }
    }

    fun resetToInput() {
        _uiState.value = UiState.Input
    }
}