package com.example.AutoGreen.viewmodels

import DeviceStatusResponse
import SensorDataResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.HttpUrlConnectionService
//import com.example.AutoGreen.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

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
        viewModelScope.launch {  // Running on Main Thread
            try {
                val response = HttpUrlConnectionService.getSensorData(deviceId) ?: emptyList()

                _sensorData.value = response
                _temperatureData.value = response.map { it.temperature }
                _humidityData.value = response.map { it.humidity }
                _soilMoistureData.value = response.map { it.soil_moisture_level }
                _waterTankData.value = response.map { it.water_level }

            } catch (e: Exception) {
                e.printStackTrace()
                _sensorData.value = emptyList()
                _temperatureData.value = emptyList()
                _humidityData.value = emptyList()
                _soilMoistureData.value = emptyList()
                _waterTankData.value = emptyList()
            }
        }
    }


    fun fetchDeviceStatus(deviceId: Int) {
        viewModelScope.launch {
            try {
                val response: DeviceStatusResponse? = withContext(Dispatchers.IO) {
                    HttpUrlConnectionService.getDeviceStatus(deviceId)
                }

                val safeResponse = response ?: DeviceStatusResponse(
                    device_id = deviceId,
                    watering_schedule = "Not Available",
                    target_temperature = 0.0f,
                    watering_mode = "Unknown",
                    heating_mode = "OFF",
                    water_level = 0.0f,
                    heater_status = "OFF",
                    vent_status = "CLOSED"
                )

                // Now safeResponse is guaranteed to be non-null
                _deviceStatus.postValue(safeResponse)
                _wateringMode.postValue(safeResponse.watering_mode)
                _heaterStatus.postValue(safeResponse.heater_status.equals("ON", ignoreCase = true))
                _ventStatus.postValue(safeResponse.vent_status.equals("OPEN", ignoreCase = true))

            } catch (e: Exception) {
                e.printStackTrace()

                // Handle API failure with a safe default response
                val fallbackResponse = DeviceStatusResponse(
                    device_id = deviceId,
                    watering_schedule = "Not Available",
                    target_temperature = 0.0f,
                    watering_mode = "Unknown",
                    heating_mode = "OFF",
                    water_level = 0.0f,
                    heater_status = "OFF",
                    vent_status = "CLOSED"
                )

                _deviceStatus.postValue(fallbackResponse)
                _wateringMode.postValue("Unknown")
                _heaterStatus.postValue(false)
                _ventStatus.postValue(false)
            }
        }
    }


}
