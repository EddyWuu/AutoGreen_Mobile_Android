package com.example.AutoGreen.network.viewmodels

import TemperatureRequest
import WaterRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AutoGreen.network.RetrofitInstance
import kotlinx.coroutines.launch

class ControlsViewModel: ViewModel() {

    fun sendManualWaterAPI(deviceId: Int, waterAmount: Int) {
        viewModelScope.launch {
            try {
                // create our command body
                val commandBody = mapOf(
                    "commandType" to "SetManualWaterAmount",
                    "amount" to waterAmount
                )
                // create request
                val request = WaterRequest(command_body = commandBody)
                // send request and get a response, check response in later code
                val response = RetrofitInstance.api.sendManualWater(deviceId, request)
                if (response.isSuccessful) {
                    Log.d("ControlsViewModel", "Success, yay")
                } else {
                    Log.e("ControlsViewModel", "Failed, nooo: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ControlsViewModel", "Failed, nooo: ${e.message}")
            }
        }
    }

    fun sendAutomaticWaterAPI(deviceId: Int, automaticWaterAmount: Int, timeInterval: Int) {
        viewModelScope.launch {
            try {
                val commandBody = mapOf(
                    "commandType" to "SetAutomaticWatering",
                    "amount" to automaticWaterAmount,
                    "interval" to timeInterval
                )
                val request = WaterRequest(command_body = commandBody)
                val response = RetrofitInstance.api.sendAutomaticWater(deviceId, request)
                if (response.isSuccessful) {
                    Log.d("ControlsViewModel", "Success, yay")
                } else {
                    Log.e("ControlsViewModel", "Failed, nooo: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ControlsViewModel", "Failed, nooo: ${e.message}")
            }
        }
    }

    fun sendTemperatureAPI(deviceId: Int, setTemperature: Int) {
        viewModelScope.launch {
            try {
                val commandBody = mapOf(
                    "commandType" to "SetTemperature",
                    "temp" to setTemperature
                )
                val request = TemperatureRequest(command_body = commandBody)
                val response = RetrofitInstance.api.sendTemperature(deviceId, request)
                if (response.isSuccessful) {
                    Log.d("ControlsViewModel", "Success, yay")
                } else {
                    Log.e("ControlsViewModel", "Failed, nooo: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ControlsViewModel", "Failed, nooo: ${e.message}")
            }
        }
    }
}