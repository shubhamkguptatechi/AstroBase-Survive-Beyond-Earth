package com.example.data.model

enum class EventSeverity {
    INFO,
    WARNING,
    CRITICAL
}

data class EventChoice(
    val id: String,
    val title: String,
    val description: String,
    val costCredits: Int = 0,
    val deltaOxygen: Float = 0f,
    val deltaWater: Float = 0f,
    val deltaFood: Float = 0f,
    val deltaEnergy: Float = 0f,
    val deltaHealth: Float = 0f,
    val deltaIntegrity: Float = 0f,
    val deltaRadiationRisk: Float = 0f,
    val scienceRationale: String
)

data class MissionEvent(
    val id: String,
    val title: String,
    val severity: EventSeverity,
    val targetDestination: Destination? = null, // null = applies to both Moon & Mars
    val description: String,
    val scienceExplanation: String,
    val choices: List<EventChoice>
)

data class EventLogEntry(
    val sol: Int,
    val title: String,
    val chosenOption: String,
    val outcomeSummary: String,
    val timestampMs: Long = System.currentTimeMillis()
)
