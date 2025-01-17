package com.example.AutoGreen.network

import com.example.AutoGreen.network.models.PlantCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// a global state so learning mode / automatic and manual mode can be managed
object LearningModeManager {

    private val _isLearning = MutableStateFlow(false)
    val isLearning: StateFlow<Boolean> get() = _isLearning

    private val _currentCategory = MutableStateFlow<PlantCategory?>(null)
    val currentCategory: StateFlow<PlantCategory?> get() = _currentCategory

    fun setLearningMode(value: Boolean) {
        _isLearning.value = value
        // need to clear category if its not learning mode
        if (!value) {
            _currentCategory.value = null
        }
        println("current mode: $isLearning")
    }

    fun setCurrentCategory(category: PlantCategory) {
        _currentCategory.value = category
        println("current category: ${category.description}")
    }
}