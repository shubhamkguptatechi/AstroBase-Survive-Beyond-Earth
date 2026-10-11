package com.example

import com.example.data.model.*
import com.example.data.scenario.MissionScenarios
import com.example.game.GameEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMissionInitialization() {
        val config = MissionConfig(
            destination = Destination.MOON,
            crewSize = 4,
            difficulty = Difficulty.STANDARD,
            targetDurationSols = 30
        )
        val state = GameEngine.initializeMission(config)

        assertEquals(1, state.currentSol)
        assertEquals(1200, state.credits)
        assertEquals(100f, state.oxygen, 0.01f)
        assertEquals(100f, state.water, 0.01f)
        assertEquals(100f, state.food, 0.01f)
        assertEquals(100f, state.crewHealth, 0.01f)
        assertEquals(MissionStatus.IN_PROGRESS, state.status)
        assertFalse(state.isGameOver)
    }

    @Test
    fun testSolAdvancementConsumesResources() {
        val config = MissionConfig(
            destination = Destination.MOON,
            crewSize = 4,
            difficulty = Difficulty.STANDARD,
            targetDurationSols = 30
        )
        val initialState = GameEngine.initializeMission(config)
        val nextState = GameEngine.advanceSol(initialState)

        assertEquals(2, nextState.currentSol)
        assertTrue(nextState.oxygen < initialState.oxygen)
        assertTrue(nextState.water < initialState.water)
        assertTrue(nextState.food < initialState.food)
        assertTrue(nextState.score > initialState.score)
    }

    @Test
    fun testUpgradeApplicationConstraints() {
        val config = MissionConfig(difficulty = Difficulty.STANDARD)
        val state = GameEngine.initializeMission(config)
        val upgrades = MissionScenarios.getDefaultUpgrades()
        val solarUpgrade = upgrades.first { it.id == "SOLAR_ARRAY" }

        val (stateAfterUpgrade, success) = GameEngine.applyUpgrade(state, solarUpgrade)
        assertTrue(success)
        assertEquals(state.credits - solarUpgrade.costCredits, stateAfterUpgrade.credits)
        assertEquals(1, stateAfterUpgrade.installedUpgrades["SOLAR_ARRAY"])
        assertTrue(stateAfterUpgrade.solarGenerationKW > state.solarGenerationKW)

        // Try to purchase with zero credits
        val poorState = stateAfterUpgrade.copy(credits = 10)
        val (stateFailed, failSuccess) = GameEngine.applyUpgrade(poorState, solarUpgrade)
        assertFalse(failSuccess)
        assertEquals(10, stateFailed.credits)
    }

    @Test
    fun testMissionFailureDetectionAndAnalysis() {
        val config = MissionConfig(destination = Destination.MARS)
        val state = GameEngine.initializeMission(config).copy(
            oxygen = 0.5f,
            currentSol = 14
        )
        // Advance sol should exhaust oxygen and trigger failure report
        val failedState = GameEngine.advanceSol(state)

        assertEquals(MissionStatus.FAILED, failedState.status)
        assertTrue(failedState.isGameOver)
        assertNotNull(failedState.failureAnalysis)

        val report = failedState.failureAnalysis!!
        assertTrue(report.primaryCause.contains("Asphyxiation") || report.exhaustedResource.contains("Oxygen"))
        assertTrue(report.tacticalAdvice.isNotEmpty())
        assertFalse(report.scienceDebrief.isBlank())
    }

    @Test
    fun testEventResolutionAppliesImpacts() {
        val config = MissionConfig()
        val state = GameEngine.initializeMission(config)
        val event = MissionScenarios.getAllEvents().first()
        val choice = event.choices.first()

        val resolved = GameEngine.resolveEvent(state, event, choice)
        assertNull(resolved.activeEmergency)
        assertTrue(resolved.eventHistory.any { it.title == event.title })
    }
}
