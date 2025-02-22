package com.example.AutoGreen.network.viewmodels

import TemperatureRequest
import WaterRequest
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.RetrofitInstance
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class ControlsViewModel: ViewModel() {

    val snackbarMessage = MutableLiveData<String?>()
    val errorMessage = MutableLiveData<String>()
    val errorMessage2 = MutableLiveData<String>()

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
                    if (response.isSuccessful) {
                        snackbarMessage.postValue("Temperature request sent successfully")
                        Log.d("ControlsViewModel", "Success, yay")
                    } else {
                        snackbarMessage.postValue("Failed to send temperature request")
                        Log.e("ControlsViewModel", "Failed, nooo: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    snackbarMessage.postValue("Failed to send temperature request")
                    Log.e("ControlsViewModel", "Failed, nooo: ${e.message}")
                }
            }
            println("sendTemperatureAPI Time Taken: $timeTaken ms")
        }
    }
}