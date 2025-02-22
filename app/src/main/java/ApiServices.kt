package com.example.AutoGreen.network

import DeviceStatusResponse
import SensorDataResponse
import TemperatureRequest
import LearningModeRequest
import WaterRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.AutoGreen.network.models.PlantInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.measureTimeMillis


object HttpUrlConnectionService {
    private const val BASE_URL = "https://autogreen-capstone.ca/"
    private const val TIMEOUT = 10000 // 10 seconds timeout

    // ✅ GET request
    private suspend fun getRequest(endpoint: String): String? = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL$endpoint")
        var connection: HttpURLConnection? = null
        var response: String? = null

        try {
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT
                readTimeout = TIMEOUT
                doInput = true
                connect()
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                response = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            } else {
                println("❌ Server error (${connection.responseCode}) for GET $endpoint")
            }
        } catch (e: IOException) {
            println("❌ Network error for GET $endpoint: ${e.message}")
        } finally {
            connection?.disconnect()
        }

        return@withContext response
    }

    // ✅ POST request
    private suspend fun postRequest(endpoint: String, requestBody: Any): Boolean = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL$endpoint")
        var connection: HttpURLConnection? = null
        var success = false

        try {
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                connectTimeout = TIMEOUT
                readTimeout = TIMEOUT
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                connect()
            }

            OutputStreamWriter(connection.outputStream).use { it.write(Gson().toJson(requestBody)) }

            success = connection.responseCode == HttpURLConnection.HTTP_OK
            if (!success) {
                println("❌ Server error (${connection.responseCode}) for POST $endpoint")
            }
        } catch (e: IOException) {
            println("❌ Network error for POST $endpoint: ${e.message}")
        } finally {
            connection?.disconnect()
        }

        return@withContext success
    }

    // ✅ Remove timing measurement from getPlants()
    suspend fun getPlants(): List<PlantInfo>? {
        val response = getRequest("api/plants") ?: return null
        return Gson().fromJson(response, object : TypeToken<List<PlantInfo>>() {}.type)
    }

    suspend fun getPlantById(plantId: Int): PlantInfo? {
        val response = getRequest("api/plants/$plantId") ?: return null
        return Gson().fromJson(response, PlantInfo::class.java)
    }

    // ✅ API methods WITH timing logs (only for commands and sensor calls)
    suspend fun getSensorData(deviceId: Int): List<SensorDataResponse>? {
        return measureApiTime("getSensorData") {
            val response = getRequest("api/sensor-data/day/$deviceId")
            response?.let { Gson().fromJson(it, object : TypeToken<List<SensorDataResponse>>() {}.type) }
        }
    }

    suspend fun getSensorHistory(deviceId: Int): List<SensorDataResponse>? {
        return measureApiTime("getSensorHistory") {
            val response = getRequest("api/sensor-data/week/$deviceId")
            response?.let { Gson().fromJson(it, object : TypeToken<List<SensorDataResponse>>() {}.type) }
        }
    }

    suspend fun getDeviceStatus(deviceId: Int): DeviceStatusResponse {
        return measureApiTime("getDeviceStatus") {
            val response = getRequest("api/device-status/$deviceId")
            response?.let { Gson().fromJson(it, DeviceStatusResponse::class.java) }
                ?: DeviceStatusResponse(
                    device_id = deviceId,
                    watering_schedule = "Not Available",
                    target_temperature = 0.0f,
                    watering_mode = "Unknown",
                    heating_mode = "OFF",
                    water_level = 0.0f,
                    heater_status = "OFF",
                    vent_status = "CLOSED"
                )
        }
    }

    suspend fun sendManualWater(deviceId: Int, request: WaterRequest): Boolean {
        return measureApiTime("sendManualWater") {
            postRequest("api/commands/$deviceId", request)
        }
    }

    suspend fun sendAutomaticWater(deviceId: Int, request: WaterRequest): Boolean {
        return measureApiTime("sendAutomaticWater") {
            postRequest("api/commands/$deviceId", request)
        }
    }

    suspend fun sendTemperature(deviceId: Int, request: TemperatureRequest): Boolean {
        return measureApiTime("sendTemperature") {
            postRequest("api/commands/$deviceId", request)
        }
    }

    suspend fun setLearningMode(deviceId: Int, request: LearningModeRequest): Boolean {
        return measureApiTime("setLearningMode") {
            postRequest("api/device-status/learning-mode/$deviceId", request)
        }
    }

    // ✅ Measure API call time (for specific calls)
    private suspend fun <T> measureApiTime(apiName: String, block: suspend () -> T): T {
        var result: T
        val elapsedTime = measureTimeMillis {
            result = block() // Store result instead of returning early
        }
        println("⏳ $apiName took $elapsedTime ms") // Now prints correctly!
        return result
    }

}
