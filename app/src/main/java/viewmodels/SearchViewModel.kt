package com.example.AutoGreen.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.LearningModeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
//import com.example.AutoGreen.network.RetrofitInstance
import com.example.AutoGreen.network.models.PlantInfo
import LearningModeRequest
import com.example.AutoGreen.network.HttpUrlConnectionService

class SearchViewModel : ViewModel() {

    // Holds the current search query, default is empty ""
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Holds search results
    private val _searchResults = MutableStateFlow<List<PlantInfo>>(emptyList())
    val searchResults: StateFlow<List<PlantInfo>> = _searchResults

    // Holds all plants fetched from the server
    private val _allPlants = MutableStateFlow<List<PlantInfo>>(emptyList())

    // Update the search query and fetch search results
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        fetchSearchResults(query)  // Synchronous call (bad)
    }

    // Fetch search results from backend
    private fun fetchSearchResults(query: String) {
        try {
            val plants = HttpUrlConnectionService.getPlants() ?: emptyList()
            _searchResults.value = plants.filter {
                it.speciesName.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _searchResults.value = emptyList()
        }
    }
    // Fetch all plants from backend
    fun fetchAllPlants() {
        try {
            val plants = HttpUrlConnectionService.getPlants() ?: emptyList()
            _allPlants.value = plants
            _searchResults.value = plants  // Initially display all plants
        } catch (e: Exception) {
            e.printStackTrace()
            _allPlants.value = emptyList()
            _searchResults.value = emptyList()
        }
    }

    // Send command to toggle learning mode
    fun sendLearningModeCommand(deviceId: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        try {
            val isLearning = LearningModeManager.isLearning.value
            val request = LearningModeRequest(isLearning = isLearning)
            val success = HttpUrlConnectionService.setLearningMode(deviceId, request)

            if (success) {
                val successMessage = "Learning mode ${if (isLearning) "enabled" else "disabled"} successfully."
                onSuccess(successMessage)
            } else {
                onError("Failed to set learning mode.")
            }
        } catch (e: Exception) {
            onError("Network error: ${e.localizedMessage}")
        }
    }

    // Test if the server is reachable
    private suspend fun testServerConnectivity() {
        try {
            val testResponse = HttpUrlConnectionService.getPlants()
            println("Server reached, ${testResponse?.size ?: 0} plants found.")
        } catch (e: Exception) {
            println("Server not connected: ${e.localizedMessage}")
        }
    }
}