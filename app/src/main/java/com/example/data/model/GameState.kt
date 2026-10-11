package com.example.data.model

import com.example.game.ResourceLevels
import com.example.game.SolResourceDelta
import com.example.game.ValidationResult
import kotlin.math.max
import kotlin.math.min

enum class MissionStatus {
    PLANNING,
    IN_PROGRESS,
    VICTORY,
    FAILED
}

data class MissionFailureReport(
    val failedSol: Int,
    val primaryCause: String,
    val rootMechanism: String,
    val exhaustedResource: String,
    val criticalDecision: String,
    val scienceDebrief: String,
    val tacticalAdvice: List<String>
)

data class GameState(
    val config: MissionConfig = MissionConfig(),
    val status: MissionStatus = MissionStatus.PLANNING,
    val currentSol: Int = 1,
    val score: Int = 0,
    val credits: Int = 1200,

    // Core ECLSS & Habitat Resources (0..100 scale)
    val oxygen: Float = 100f,
    val water: Float = 100f,
    val food: Float = 100f,
    val energy: Float = 100f, // percentage of battery buffer
    val batteryStoredKWh: Float = 120f,
    val maxBatteryKWh: Float = 120f,

    // Environmental & Physiological telemetry
    val crewHealth: Float = 100f,
    val radiationRisk: Float = 15f, // Current ambient exposure percentage
    val habitatIntegrity: Float = 100f,
    val foodProductionCapacity: Float = 0f, // % food produced per sol

    // Power budget live readouts
    val solarGenerationKW: Float = 28f,
    val powerDemandKW: Float = 22f,

    // Environmental state flags
    val solarDustAttenuation: Float = 1.0f, // 1.0 = clean, 0.4 = dust storm
    val isEclipseOrNight: Boolean = false,

    // Upgrades and Events
    val installedUpgrades: Map<String, Int> = emptyMap(), // upgradeId -> count
    val activeEmergency: MissionEvent? = null,
    val eventHistory: List<EventLogEntry> = emptyList(),
    val failureAnalysis: MissionFailureReport? = null,
    val lastResourceDelta: SolResourceDelta? = null
) {
    val isGameOver: Boolean get() = status == MissionStatus.FAILED || status == MissionStatus.VICTORY
    val netPowerBalanceKW: Float get() = solarGenerationKW - powerDemandKW
    val isPowerDeficit: Boolean get() = netPowerBalanceKW < 0f

    fun toResourceLevels(): ResourceLevels {
        return ResourceLevels(
            oxygen = oxygen,
            water = water,
            food = food,
            energy = energy,
            crewHealth = crewHealth,
            radiationRisk = radiationRisk,
            habitatIntegrity = habitatIntegrity
        )
    }

    /**
     * Enforces domain invariant bounds: resources cannot fall below 0 and stay within [0, 100].
     */
    fun withClampedResources(): GameState {
        val clampedStored = batteryStoredKWh.coerceIn(0f, max(1f, maxBatteryKWh))
        val computedEnergy = if (maxBatteryKWh > 0f) {
            (clampedStored / maxBatteryKWh * 100f).coerceIn(0f, 100f)
        } else 0f

        return copy(
            oxygen = oxygen.coerceIn(0f, 100f),
            water = water.coerceIn(0f, 100f),
            food = food.coerceIn(0f, 100f),
            energy = computedEnergy,
            batteryStoredKWh = clampedStored,
            crewHealth = crewHealth.coerceIn(0f, 100f),
            radiationRisk = radiationRisk.coerceIn(0f, 100f),
            habitatIntegrity = habitatIntegrity.coerceIn(0f, 100f),
            credits = max(0, credits)
        )
    }

    fun validate(): ValidationResult {
        return toResourceLevels().validate()
    }
}
