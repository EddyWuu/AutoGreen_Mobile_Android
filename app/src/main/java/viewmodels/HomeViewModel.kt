package com.example.AutoGreen.viewmodels

import DeviceStatusResponse
import SensorDataResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel: ViewModel() {

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

    private val _deviceStatus = MutableLiveData<DeviceStatusResponse>()
    val deviceStatus: LiveData<DeviceStatusResponse> get() = _deviceStatus

    private val _wateringMode = MutableLiveData<String>()
    val wateringMode: LiveData<String> get() = _wateringMode

    private val _ventStatus = MutableLiveData<Boolean>()
    val ventStatus: LiveData<Boolean> get() = _ventStatus

    private val _heaterStatus = MutableLiveData<Boolean>()
    val heaterStatus: LiveData<Boolean> get() = _heaterStatus


    fun fetchSensorData(deviceId: Int) {
        viewModelScope.launch {
            try {
                // call our api func to get sensor data and store as response
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getSensorData(deviceId)
                }
                _sensorData.postValue(response)

                // extract our data from our response
                _temperatureData.postValue(response.map { it.temperature })
                _humidityData.postValue(response.map { it.humidity })
                _soilMoistureData.postValue(response.map { it.soil_moisture_level })
                _waterTankData.postValue(response.map { it.water_level })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchDeviceStatus(deviceId: Int) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getDeviceStatus(deviceId)
                }
                _deviceStatus.postValue(response)

                _wateringMode.postValue(response.watering_mode)
                _heaterStatus.postValue(response.heater_status.equals("ON", ignoreCase = true))
                _ventStatus.postValue(response.vent_status.equals("OPEN", ignoreCase = true))

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
