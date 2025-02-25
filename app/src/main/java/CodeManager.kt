package com.example.AutoGreen.network

import com.example.AutoGreen.network.models.PlantCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// a global state so learning mode / automatic and manual mode can be managed
object LearningModeManager {

    private val _isLearning = MutableStateFlow(false)
    val isLearning: StateFlow<Boolean> get() = _isLearning

    private val _moistureLevel = MutableStateFlow<Int?>(null)
    val moistureLevel: StateFlow<Int?> get() = _moistureLevel

    fun setLearningMode(value: Boolean) {
        _isLearning.value = value
        if (!value) {
            // clear moisture level if not in learning mode
            _moistureLevel.value = null
        }
        println("Learning mode: $value, Moisture level cleared: ${!value}")
    }

    // New function to update learning mode based on watering mode string
    fun updateFromWateringMode(wateringMode: String) {
        // If wateringMode equals "Learning", set learning mode to true, otherwise false (e.g., "Idle")
        val isLearningMode = wateringMode.equals("Learning", ignoreCase = true)
        setLearningMode(isLearningMode)
    }


}