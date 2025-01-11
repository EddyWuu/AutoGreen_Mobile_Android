package com.example.AutoGreen.network.models

enum class PlantCategory(val description: String) {
    DESERT_DRY("Desert-Dry (0-20%)"),
    ARID_MILD("Arid-Mild (21-40%)"),
    MODERATELY_WET("Moderately-Wet (41-60%)"),
    OASIS_MOIST("Oasis-Moist (61-80%)");

    companion object {
        // function to determine the category based on the moist level
        fun categoryForMoistureLevel(moistureLevel: Int): PlantCategory {
            return when (moistureLevel) {
                in 0..20 -> DESERT_DRY
                in 21..40 -> ARID_MILD
                in 41..60 -> MODERATELY_WET
                in 61..80 -> OASIS_MOIST
                else -> throw IllegalArgumentException("Moisture level out of bounds")
            }
        }
    }
}

// Data class for representing a plant
data class PlantInfo(
    val plantId: Int,
    val speciesName: String,
    val minTempRange: Int,
    val maxTempRange: Int,
    val wateringFrequency: String,
    val wateringAmount: Int,
    val soilMoistureLevel: Int
) {
    // function to get the category of the plant based on its soil moist level
    val category: PlantCategory
        get() = PlantCategory.categoryForMoistureLevel(soilMoistureLevel)
}
