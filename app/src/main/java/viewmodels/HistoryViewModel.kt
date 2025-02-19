package com.example.AutoGreen.viewmodels

import SensorDataResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.HttpUrlConnectionService
//import com.example.AutoGreen.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel: ViewModel() {

    // MutableLiveData to hold the list of sensor data responses
    private val _sensorData = MutableLiveData<List<SensorDataResponse>>()
    val sensorData: LiveData<List<SensorDataResponse>> get() = _sensorData

    // mutable live data for each data, a list of each data
    private val _temperatureData = MutableLiveData<List<Float>>()
    val temperatureData: LiveData<List<Float>> get() = _temperatureData

    private val _humidityData = MutableLiveData<List<Float>>()
    val humidityData: LiveData<List<Float>> get() = _humidityData

    private val _soilMoistureData = MutableLiveData<List<Float>>()
    val soilMoistureData: LiveData<List<Float>> get() = _soilMoistureData

    private val _waterTankData = MutableLiveData<List<Float>>()
    val waterTankData: LiveData<List<Float>> get() = _waterTankData


    fun fetchSensorHistory(deviceId: Int) {
        viewModelScope.launch {
            try {
                // Call API function using HttpURLConnection (runs in Dispatchers.IO)
                val response = withContext(Dispatchers.IO) {
                    HttpUrlConnectionService.getSensorHistory(deviceId)
                } ?: emptyList() // Provide default empty list if null

                _sensorData.postValue(response)

                // Extract and post each type of data
                _temperatureData.postValue(response.map { it.temperature })
                _humidityData.postValue(response.map { it.humidity })
                _soilMoistureData.postValue(response.map { it.soil_moisture_level })
                _waterTankData.postValue(response.map { it.water_level })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}
