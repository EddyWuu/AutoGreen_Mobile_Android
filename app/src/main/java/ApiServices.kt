package com.example.AutoGreen.network

import SensorDataResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


// retrofit instance to handle network requests from our server url
object RetrofitInstance {
    private const val BASE_URL = "https://valid-octagon-429920-c3.ue.r.appspot.com/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {

    // api call on this endpoint:
    @GET("api/sensor-data/day/{device_id}")
    // get the data that is of form SensorDataResponse
    suspend fun getSensorData(@Path("device_id") deviceId: Int): List<SensorDataResponse>
}

