package com.example.AutoGreen.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// a global state so learning mode / automatic and manual mode can be managed
object LearningModeManager {
    private val _isLearning = MutableStateFlow(false)
    val isLearning: StateFlow<Boolean> get() = _isLearning

    fun setLearningMode(value: Boolean) {
        _isLearning.value = value
    }
}