package com.example.AutoGreen.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.LearningModeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.AutoGreen.network.RetrofitInstance
import com.example.AutoGreen.network.models.PlantInfo
import LearningModeRequest

class SearchViewModel : ViewModel() {

    // hold the current search query, default empty ""
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // filter for results of search
    private val _searchResults = MutableStateFlow<List<PlantInfo>>(emptyList())
    val searchResults: StateFlow<List<PlantInfo>> = _searchResults

    // hold all plants fetched from the server
    private val _allPlants = MutableStateFlow<List<PlantInfo>>(emptyList())

    // fetch from backend
    private val apiService = RetrofitInstance.api

    // for updating the search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        fetchSearchResults(query)
    }

    // fetching the search results, in which we grab from apiservice which takes from BE
    private fun fetchSearchResults(query: String) {

        viewModelScope.launch {
            try {
                testServerConnectivity()
                println("helloooooooo")

                // fetch all plants
                val plants = apiService.getPlants()
                println("Retrieved plants from server:")
                plants.forEach { println("Plantssss: $it") }

                // filter
                _searchResults.value = plants.filter {
                    it.speciesName.contains(query, ignoreCase = true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.value = emptyList()
            }
        }
    }

    // fetch all plants
    fun fetchAllPlants() {
        viewModelScope.launch {
            try {
                val plants = apiService.getPlants()
                _allPlants.value = plants
                _searchResults.value = plants // Initially display all plants

                // Log all plants fetched
                println("Fetched all plants from server:")
                plants.forEach { println(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                _allPlants.value = emptyList()
                _searchResults.value = emptyList()
            }
        }
    }

    // send command for if learning is set and what category
    fun sendLearningModeCommand(deviceId: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val isLearning = LearningModeManager.isLearning.value
                val moistureLevel = LearningModeManager.moistureLevel.value

                val request = LearningModeRequest(
                    isLearning = isLearning,
                    moistureLevel = if (isLearning) moistureLevel else null
                )

                val response = RetrofitInstance.api.setLearningMode(
                    deviceId = deviceId,
                    request = request
                )

                if (response.isSuccessful) {
                    val successMessage = "Learning mode ${if (isLearning) "enabled" else "disabled"} successfully."
                    onSuccess(successMessage)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Failed to set learning mode"
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                onError("Network error: ${e.localizedMessage}")
            }
        }
    }



    private suspend fun testServerConnectivity() {
        try {
            // Quick connectivity check using /api/plants
            val testResponse = apiService.getPlants()
            println("Server is reached, ${testResponse.size} plants.")
        } catch (e: Exception) {
            println("Server not connecteddddddddddd, ${e.localizedMessage}")
        }
    }
}
