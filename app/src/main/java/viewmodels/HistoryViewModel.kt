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

class HistoryViewModel : ViewModel() {

    private val _sensorData = MutableLiveData<List<SensorDataResponse>>()
    val sensorData: LiveData<List<SensorDataResponse>> get() = _sensorData

    private val _temperatureData = MutableLiveData<List<Float>>()
    val temperatureData: LiveData<List<Float>> get() = _temperatureData

    private val _humidityData = MutableLiveData<List<Float>>()
    val humidityData: LiveData<List<Float>> get() = _humidityData

    private val _soilMoistureData = MutableLiveData<List<Float>>()
    val soilMoistureData: LiveData<List<Float>> get() = _soilMoistureData

    private val _waterTankData = MutableLiveData<List<Float>>()
    val waterTankData: LiveData<List<Float>> get() = _waterTankData


    fun fetchSensorHistory(deviceId: Int) {
        try {
            val response = HttpUrlConnectionService.getSensorHistory(deviceId) ?: emptyList()

            _sensorData.value = response
            _temperatureData.value = response.map { it.temperature }
            _humidityData.value = response.map { it.humidity }
            _soilMoistureData.value = response.map { it.soil_moisture_level }
            _waterTankData.value = response.map { it.water_level }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
