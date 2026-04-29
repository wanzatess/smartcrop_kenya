package com.example.smartcropkenya

import androidx.lifecycle.ViewModel
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

class SmartCropViewModel(
    private val repository: SmartCropRepository = MockSmartCropRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Input)
    val uiState = _uiState.asStateFlow()

    private val _locations = MutableStateFlow<List<SubcountyLocation>>(emptyList())
    val locations = _locations.asStateFlow()

    init {
        viewModelScope.launch {
            _locations.value = repository.getLocations()
        }
    }

    fun submitData(location: SubcountyLocation, n: Int, p: Int, k: Int, ph: Double) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getCropPrediction(location, SoilMetrics(n, p, k, ph))
            result.onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Failed") }
        }
    }

    fun resetToInput() { _uiState.value = UiState.Input }
}