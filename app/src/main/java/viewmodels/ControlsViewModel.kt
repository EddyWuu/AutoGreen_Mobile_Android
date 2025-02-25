package com.example.AutoGreen.network.viewmodels

import DeviceStatusResponse
import SetManualRequest
import SetTempRequest
import SetWaterFreqRequest
import TemperatureRequest
import WaterRequest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.minutes

class ControlsViewModel: ViewModel() {




    val snackbarMessage = MutableLiveData<String?>()
    val errorMessage = MutableLiveData<String>()
    val errorMessage2 = MutableLiveData<String>()

    private val _setTempValue = MutableLiveData<String>()
    val setTempValue: LiveData<String> = _setTempValue

    private val _deviceStatus = MutableLiveData<DeviceStatusResponse>()
    val deviceStatus: LiveData<DeviceStatusResponse> get() = _deviceStatus

    init {
        fetchSetTemperature()
        fetchDeviceStatus(2)
    }

    fun fetchDeviceStatus(deviceId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getDeviceStatus(deviceId)
                }
                _deviceStatus.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchSetTemperature() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getDeviceStatus(2)
                val fetchedTemp = response.target_temperature?.toInt()?.toString() ?: "0"

                Log.d("ControlsViewModel", "Fetched Target Temperature from Backend: $fetchedTemp")

                _setTempValue.postValue(fetchedTemp)
            } catch (e: Exception) {
                Log.e("ControlsViewModel", "Error fetching temperature: ${e.message}")
                _setTempValue.postValue("0")
            }
        }
    }


    fun validateManualWaterAmount(waterAmount: String): Boolean {
        val number = waterAmount.toIntOrNull()
        when {
            number == null -> {
                errorMessage.value = "Please enter a valid number."
                return false
            }
            number > 500 -> {
                errorMessage.value = "The value cannot exceed 500 ml."
                return false
            }
            number < 100 -> {
                errorMessage.value = "The value cannot be lower than 100 ml."
                return false
            }
            else -> {
                errorMessage.value = ""
                return true
            }
        }
    }

    fun validateAutomaticWatering(interval: String, amount: String): Boolean {
        val intervalNum = interval.toIntOrNull()
        val amountNum = amount.toIntOrNull()

        when {
            intervalNum == null -> {
                errorMessage.value = "Please enter a valid number for interval."
                return false
            }
            amountNum == null -> {
                errorMessage2.value = "Please enter a valid number for amount."
                return false
            }
            amountNum > 500 -> {
                errorMessage2.value = "The value cannot exceed 500 ml."
                return false
            }
            amountNum < 100 -> {
                errorMessage2.value = "The value cannot be lower than 100 ml."
                return false
            }
            else -> {
                errorMessage.value = ""
                errorMessage2.value = ""
                return true
            }
        }
    }

    fun validateTemperature(temperature: String): Boolean {
        val number = temperature.toIntOrNull()
        when {
            number == null -> {
                errorMessage.value = "Please enter a valid number."
                return false
            }
            number > 50 -> {
                errorMessage.value = "The value cannot exceed 50°C."
                return false
            }
            number < 0 -> {
                errorMessage.value = "Negative values are not allowed."
                return false
            }
            else -> {
                errorMessage.value = ""
                return true
            }
        }
    }

    fun convertIntervalToMinutes(interval: Int, unit: String): Int {
        return when (unit) {
            "Minutes" -> interval
            "Hours" -> interval * 60
            "Days" -> interval * 60 * 24
            "Weeks" -> interval * 60 * 24 * 7
            else -> interval
        }
    }

    fun sendManualWaterAPI(deviceId: Int, waterAmount: Int) {
        viewModelScope.launch {
            val timeTaken = measureTimeMillis {
                try {
                    val commandBody = mapOf(
                        "commandType" to "SetManualWaterAmount",
                        "amount" to waterAmount
                    )
                    val request = WaterRequest(command_body = commandBody)
                    val response = RetrofitInstance.api.sendManualWater(deviceId, request)
                    if (response.isSuccessful) {
                        val setManualRequest = SetManualRequest(
                            watering_mode = "Manual"
                        )
                        val response = RetrofitInstance.api.updateManual(2, setManualRequest)
                        snackbarMessage.postValue("Manual water request sent successfully")
                        Log.d("ControlsViewModel", "Success, yay")
                    } else {
                        snackbarMessage.postValue("Failed to send manual water request")
                        Log.e("ControlsViewModel", "Failed, nooo: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    snackbarMessage.postValue("Failed to send manual water request")
                    Log.e("ControlsViewModel", "Failed, nooo: ${e.message}")
                }
            }
            println("sendManualWaterAPI Time Taken: $timeTaken ms")
        }
    }

    fun sendAutomaticWaterAPI(deviceId: Int, automaticWaterAmount: Int, timeInterval: Int) {
        viewModelScope.launch {
            val timeTaken = measureTimeMillis {
                try {
                    val commandBody = mapOf(
                        "commandType" to "SetAutomaticWatering",
                        "amount" to automaticWaterAmount,
                        "interval" to timeInterval
                    )
                    val request = WaterRequest(command_body = commandBody)
                    val response = RetrofitInstance.api.sendAutomaticWater(deviceId, request)

                    if (response.isSuccessful) {
                        val freqRequest = SetWaterFreqRequest(
                            watering_mode = "Automatic",
                            watering_amount = automaticWaterAmount,
                            watering_frequency = timeInterval
                        )
                        _deviceStatus.postValue(
                            _deviceStatus.value?.copy(
                                watering_amount = automaticWaterAmount,
                                watering_frequency = timeInterval
                            )
                        )
                        fetchDeviceStatus(deviceId)
                        snackbarMessage.postValue("Automatic water request sent successfully")

                        val freqResponse = RetrofitInstance.api.updateSetFreq(deviceId, freqRequest)
                        if (freqResponse.isSuccessful) {
                            Log.d("ControlsViewModel", "Set frequency updated successfully")
                        } else {
                            Log.e(
                                "ControlsViewModel",
                                "Set frequency update failed: ${freqResponse.errorBody()?.string()}"
                            )
                        }

                        snackbarMessage.postValue("Automatic water request sent successfully")
                        Log.d("ControlsViewModel", "Success, yay")
                    } else {
                        snackbarMessage.postValue("Failed to send automatic water request")
                        Log.e("ControlsViewModel", "Failed, nooo: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    snackbarMessage.postValue("Failed to send automatic water request")
                    Log.e("ControlsViewModel", "Failed, nooo: ${e.message}")
                }
            }
            println("sendAutomaticWaterAPI Time Taken: $timeTaken ms")
        }
    }

    fun sendTemperatureAPI(deviceId: Int, setTemperature: Int) {
        viewModelScope.launch {
            val timeTaken = measureTimeMillis {
                try {
                    val commandBody = mapOf(
                        "commandType" to "SetTemperature",
                        "temp" to setTemperature
                    )
                    val request = TemperatureRequest(command_body = commandBody)
                    val response = RetrofitInstance.api.sendTemperature(deviceId, request)

                    val setTempResponse = RetrofitInstance.api.updateSetTemp(deviceId, SetTempRequest(target_temperature = setTemperature))

                    if (response.isSuccessful && setTempResponse.isSuccessful) {
                        fetchSetTemperature()
                        snackbarMessage.postValue("Temperature request sent successfully")
                        Log.d("ControlsViewModel", "Both API calls succeeded")
                    } else {
                        val errorResponse1 = response.errorBody()?.string()
                        val errorResponse2 = setTempResponse.errorBody()?.string()
                        snackbarMessage.postValue("Failed to send temperature request")
                        Log.e("ControlsViewModel", "Failed: $errorResponse1, $errorResponse2")
                    }
                } catch (e: Exception) {
                    snackbarMessage.postValue("Failed to send temperature request")
                    Log.e("ControlsViewModel", "Failed, nooo: ${e.message}")
                }
            }
            println("sendTemperatureAPI Time Taken: $timeTaken ms")
        }
    }

    fun formatTimeInterval(minutes: Int): String {
        return when {
            minutes >= 10080 -> "${minutes / 10080} weeks" // 10080 minutes = 1 week
            minutes >= 1440 -> "${minutes / 1440} days"  // 1440 minutes = 1 day
            minutes >= 60 -> "${minutes / 60} hours"  // 60 minutes = 1 hour
            else -> "$minutes minutes"
        }
    }

}