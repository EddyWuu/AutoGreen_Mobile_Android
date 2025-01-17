package com.example.AutoGreen.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.LearningModeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.AutoGreen.network.RetrofitInstance
import com.example.AutoGreen.network.models.PlantInfo

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
    fun sendLearningModeCommand(deviceId: Int) {
        viewModelScope.launch {
            try {
                val isLearning = LearningModeManager.isLearning.value
                val category = LearningModeManager.currentCategory.value?.description ?: "None"

                // making command_body as a JSON string
                val request = mapOf(
                    "isLearning" to isLearning,
                    "category" to category
                )

                val response = RetrofitInstance.api.setLearningMode(
                    deviceId = deviceId,
                    request = request
                )

                if (response.isSuccessful) {
                    println("Command sent successfully: ${response.body()}")
                } else {
                    println("Failed to send command: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Error sending command: ${e.message}")
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
