
data class SensorDataResponse(
    val data_id: Int,
    val device_id: Int,
    val timestamp: String,
    val soil_moisture_level: Float,
    val temperature: Float,
    val humidity: Float,
    val water_level: Float
)

data class DeviceStatusResponse(
    val device_id: Int,
    val watering_schedule: Map<String, Any>?,
    val target_temperature: Float?,
    val watering_mode: String,
    val heating_mode: String?,
    val water_level: Float?,
    val heater_status: String,
    val vent_status: String
)
