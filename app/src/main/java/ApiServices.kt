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


// retrofit instance to handle network requests from our server url
object RetrofitInstance {
//    private const val BASE_URL = "https://valid-octagon-429920-c3.ue.r.appspot.com/"
    private const val BASE_URL = "https://autogreen-capstone.ca/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {

    // getting sensor data, api call on this endpoint:
    @GET("api/sensor-data/day/{device_id}")
    // get the data that is of form SensorDataResponse
    suspend fun getSensorData(@Path("device_id") deviceId: Int): List<SensorDataResponse>

    // getting sensor data, api call on this endpoint:
    @GET("api/sensor-data/week/{device_id}")
    // get the data that is of form SensorDataResponse
    suspend fun getSensorHistory(@Path("device_id") deviceId: Int): List<SensorDataResponse>

    // get device status
    @GET("api/device-status/{device_id}")
    suspend fun getDeviceStatus(@Path("device_id") deviceId: Int): DeviceStatusResponse

    // sending manual watering request api call on this end point:
    @POST("api/commands/{device_id}")
    // send manual watering data to server endpoint, body in the form of model waterrequest
    suspend fun sendManualWater(@Path("device_id") deviceId: Int, @Body request: WaterRequest): Response<Unit>

    // automatic watering
    @POST("api/commands/{device_id}")
    // send automatic watering data to this endpoint, same as above, just a different func call for future changes
    suspend fun sendAutomaticWater(@Path("device_id") deviceId: Int, @Body request: WaterRequest): Response<Unit>

    // setting temperature
    @POST("api/commands/{device_id}")
    suspend fun sendTemperature(@Path("device_id") deviceId: Int, @Body request: TemperatureRequest): Response<Unit>

    // sending info on if learning mode is on and what category/level
    @POST("api/device-status/learning-mode/{device_id}")
    suspend fun setLearningMode(@Path("device_id") deviceId: Int, @Body request: LearningModeRequest): Response<Unit>

    // get general plant info
    @GET("api/plants")
    suspend fun getPlants(): List<PlantInfo>

    @GET("api/plants/{plant_id}")
    suspend fun getPlantById(@Path("plant_id") plantId: Int): PlantInfo
}

