import com.google.gson.annotations.SerializedName
import kotlin.time.Duration

data class WaterRequest(
    val command_body: Map<String, Any>,
    val command_status: String = "pending"
)

data class TemperatureRequest(
    val command_body: Map<String, Any>,
    val command_status: String = "pending"
)

data class LearningModeRequest(
    val watering_mode: String,
    val plant_name: String
)

data class SetTempRequest(
    val target_temperature: Int
)

data class SetWaterFreqRequest(
    val watering_amount: Int?,
    val watering_frequency: Int?
)
