
data class WaterRequest (
    val command_body: Map<String, Any>,
    val command_status: String = "pending"
)