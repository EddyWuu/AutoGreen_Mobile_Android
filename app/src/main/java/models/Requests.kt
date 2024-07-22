
data class WaterRequest(
    val command_body: Map<String, Any>,
    val command_status: String = "pending"
)

data class TemperatureRequest(
    val command_body: Map<String, Any>,
    val command_status: String = "pending"
)