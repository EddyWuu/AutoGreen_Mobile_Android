
data class SensorDataResponse(
    val data_id: Int,
    val device_id: Int,
    val timestamp: String,
    val soil_moisture_level: Float,
    val temperature: Float,
    val humidity: Float,
    val water_level: Float
)
