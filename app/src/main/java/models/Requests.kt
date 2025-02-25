import com.google.gson.annotations.SerializedName

data class WaterRequest(
    val command_body: Map<String, Any>,
    val command_status: String = "pending"
)

data class TemperatureRequest(
    val command_body: Map<String, Any>,
    val command_status: String = "pending"
)

data class LearningModeRequest(
    val is_learning_mode: Boolean,
    val plant_name: String
)

data class SetTempRequest(
    val target_temperature: Int
)
