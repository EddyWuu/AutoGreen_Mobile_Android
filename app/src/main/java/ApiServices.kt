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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


object HttpUrlConnectionService {
    private const val BASE_URL = "https://autogreen-capstone.ca/"

    private fun getRequest(endpoint: String): String? {
        val url = "$BASE_URL$endpoint"
        println("🌐 Sending GET request to: $url")

        var connection: HttpURLConnection? = null
        return try {
            connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doInput = true

            println("📡 Response Code: ${connection.responseCode}")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                // ✅ Ensure InputStream is properly read
                val inputStream = connection.inputStream
                if (inputStream == null) {
                    println("❌ InputStream is null")
                    return null
                }

                val reader = BufferedReader(InputStreamReader(inputStream))
                val responseBuilder = StringBuilder()

                println("📥 Reading response data...")
                reader.useLines { lines -> lines.forEach { responseBuilder.append(it).append("\n") } }

                val fullResponse = responseBuilder.toString().trim()
                println("✅ API Raw Response (FULL): '$fullResponse'")

                if (fullResponse.isBlank()) {
                    println("❌ API returned an empty response")
                    return null
                }
                fullResponse
            } else {
                println("❌ Server returned error: ${connection.responseCode}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Network Error: ${e.message ?: "Unknown error"}")
            null
        } finally {
            connection?.disconnect()
        }
    }




    private fun postRequest(endpoint: String, requestBody: Any): Boolean {
        val url = URL("$BASE_URL$endpoint")
        var connection: HttpURLConnection? = null
        return try {
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doInput = true
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            val json = Gson().toJson(requestBody)
            OutputStreamWriter(connection.outputStream).use { it.write(json) }

            connection.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            connection?.disconnect()
        }
    }

    fun getSensorData(deviceId: Int): List<SensorDataResponse>? {
        val response = getRequest("api/sensor-data/day/$deviceId") ?: return null
        return Gson().fromJson(response, object : TypeToken<List<SensorDataResponse>>() {}.type)
    }

    fun getSensorHistory(deviceId: Int): List<SensorDataResponse>? {
        val response = getRequest("api/sensor-data/week/$deviceId") ?: return null
        return Gson().fromJson(response, object : TypeToken<List<SensorDataResponse>>() {}.type)
    }

    fun getDeviceStatus(deviceId: Int): DeviceStatusResponse {
        val response = getRequest("api/device-status/$deviceId") ?: return DeviceStatusResponse(
            device_id = deviceId,
            watering_schedule = "Not Available",
            target_temperature = 0.0f,
            watering_mode = "Unknown",
            heating_mode = "OFF",
            water_level = 0.0f,
            heater_status = "OFF",
            vent_status = "CLOSED"
        )

        return try {
            Gson().fromJson(response, DeviceStatusResponse::class.java) ?: DeviceStatusResponse(
                device_id = deviceId,
                watering_schedule = "Not Available",
                target_temperature = 0.0f,
                watering_mode = "Unknown",
                heating_mode = "OFF",
                water_level = 0.0f,
                heater_status = "OFF",
                vent_status = "CLOSED"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            DeviceStatusResponse(
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


    fun sendManualWater(deviceId: Int, request: WaterRequest): Boolean {
        return postRequest("api/commands/$deviceId", request)
    }

    fun sendAutomaticWater(deviceId: Int, request: WaterRequest): Boolean {
        return postRequest("api/commands/$deviceId", request)
    }

    fun sendTemperature(deviceId: Int, request: TemperatureRequest): Boolean {
        return postRequest("api/commands/$deviceId", request)
    }

    fun setLearningMode(deviceId: Int, request: LearningModeRequest): Boolean {
        return postRequest("api/device-status/learning-mode/$deviceId", request)
    }

    fun getPlants(): List<PlantInfo>? {
        val response = getRequest("api/plants")

        if (response == null) {
            println("❌ No response received from API")
            return null
        }

        println("✅ Raw JSON response from API (Before Parsing): $response") // Log full response

        return try {
            val plantListType = object : TypeToken<List<PlantInfo>>() {}.type
            val plants: List<PlantInfo> = Gson().fromJson(response, plantListType)

            println("✅ Successfully parsed plant data: $plants") // Debug log
            plants
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ JSON Parsing Error: ${e.message}")
            null
        }
    }




    fun getPlantById(plantId: Int): PlantInfo? {
        val response = getRequest("api/plants/$plantId") ?: return null
        return Gson().fromJson(response, PlantInfo::class.java)
    }
}