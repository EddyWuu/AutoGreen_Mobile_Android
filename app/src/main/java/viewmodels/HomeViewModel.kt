package com.example.AutoGreen.viewmodels

import SensorDataResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel: ViewModel() {

    // MutableLiveData to hold the list of sensor data responses
    private val _temperatureData = MutableLiveData<List<SensorDataResponse>>()

    // LiveData to expose the temperature data to the UI
    val temperatureData: LiveData<List<SensorDataResponse>> get() = _temperatureData

    fun fetchTemperature(deviceId: Int) {
        // launch a coroutine in the VMscope
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    // Make the network call to get temp
                    RetrofitInstance.api.getTemperature(1)
                }
                // post results to live data
                _temperatureData.postValue(response)
            } catch (e: Exception) {
                // Handle any errors that might occur during the network request
                e.printStackTrace()
            }
        }
    }
}
