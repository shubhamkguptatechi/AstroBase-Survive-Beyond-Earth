package com.example.game

/**
 * Normalized 0..100 resource levels representing the foundational life support
 * and environmental telemetry of an extraterrestrial outpost.
 */
data class ResourceLevels(
    val oxygen: Float = 100f,
    val water: Float = 100f,
    val food: Float = 100f,
    val energy: Float = 100f,
    val crewHealth: Float = 100f,
    val radiationRisk: Float = 15f,
    val habitatIntegrity: Float = 100f
) {
    /**
     * Enforces domain invariant bounds: all values must stay within [0.0, 100.0].
     */
    fun clamped(): ResourceLevels {
        return copy(
            oxygen = oxygen.coerceIn(0f, 100f),
            water = water.coerceIn(0f, 100f),
            food = food.coerceIn(0f, 100f),
            energy = energy.coerceIn(0f, 100f),
            crewHealth = crewHealth.coerceIn(0f, 100f),
            radiationRisk = radiationRisk.coerceIn(0f, 100f),
            habitatIntegrity = habitatIntegrity.coerceIn(0f, 100f)
        )
    }

    /**
     * Validates that all resource values are within legal physical constraints.
     */
    fun validate(): ValidationResult {
        val violations = mutableListOf<String>()
        if (oxygen !in 0f..100f) violations.add("Oxygen $oxygen outside [0, 100]")
        if (water !in 0f..100f) violations.add("Water $water outside [0, 100]")
        if (food !in 0f..100f) violations.add("Food $food outside [0, 100]")
        if (energy !in 0f..100f) violations.add("Energy $energy outside [0, 100]")
        if (crewHealth !in 0f..100f) violations.add("Crew Health $crewHealth outside [0, 100]")
        if (radiationRisk !in 0f..100f) violations.add("Radiation Risk $radiationRisk outside [0, 100]")
        if (habitatIntegrity !in 0f..100f) violations.add("Habitat Integrity $habitatIntegrity outside [0, 100]")

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}

sealed interface ValidationResult {
    object Valid : ValidationResult
    data class Invalid(val errors: List<String>) : ValidationResult
}

/**
 * Detailed breakdown of resource consumption and recovery per Sol.
 * Allows the learner to observe exactly how decisions affect ECLSS balances.
 */
data class SolResourceDelta(
    val sol: Int,
    val oxygenConsumed: Float,
    val oxygenRecovered: Float,
    val netOxygenDelta: Float,

    val waterConsumed: Float,
    val waterRecovered: Float,
    val netWaterDelta: Float,

    val foodConsumed: Float,
    val foodHarvested: Float,
    val netFoodDelta: Float,

    val solarGenerationKW: Float,
    val powerDemandKW: Float,
    val netPowerKW: Float,
    val batteryStoredKWhDelta: Float,

    val crewHealthDelta: Float,
    val habitatIntegrityDelta: Float,
    val radiationExposureRisk: Float,

    val creditsEarned: Int,
    val scientificNotes: List<String>
)
