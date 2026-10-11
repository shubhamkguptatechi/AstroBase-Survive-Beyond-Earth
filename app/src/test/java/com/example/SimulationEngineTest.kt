package com.example

import com.example.data.model.*
import com.example.data.scenario.MissionScenarios
import com.example.game.*
import org.junit.Assert.*
import org.junit.Test

class SimulationEngineTest {

    @Test
    fun testResourceLevelsValidationAndClamping() {
        val invalidLevels = ResourceLevels(
            oxygen = -15f,
            water = 120f,
            food = 50f,
            energy = 80f,
            crewHealth = 90f,
            radiationRisk = 10f,
            habitatIntegrity = 100f
        )
        val validation = invalidLevels.validate()
        assertTrue(validation is ValidationResult.Invalid)

        val clamped = invalidLevels.clamped()
        assertEquals(0f, clamped.oxygen, 0.001f)
        assertEquals(100f, clamped.water, 0.001f)
        assertTrue(clamped.validate() is ValidationResult.Valid)
    }

    @Test
    fun testFinishedMissionBlocksFurtherConsumption() {
        val config = MissionConfig()
        val runningState = GameEngine.initializeMission(config)
        val failedState = runningState.copy(status = MissionStatus.FAILED, oxygen = 50f, currentSol = 10)

        val stateAfterAdvance = GameEngine.advanceSol(failedState)
        assertEquals(10, stateAfterAdvance.currentSol)
        assertEquals(50f, stateAfterAdvance.oxygen, 0.001f)
        assertEquals(MissionStatus.FAILED, stateAfterAdvance.status)

        val victoryState = runningState.copy(status = MissionStatus.VICTORY, water = 60f, currentSol = 30)
        val victoryAfterAdvance = GameEngine.advanceSol(victoryState)
        assertEquals(30, victoryAfterAdvance.currentSol)
        assertEquals(60f, victoryAfterAdvance.water, 0.001f)
        assertEquals(MissionStatus.VICTORY, victoryAfterAdvance.status)
    }

    @Test
    fun testActiveEmergencyBlocksAdvanceSol() {
        val config = MissionConfig()
        val runningState = GameEngine.initializeMission(config)
        val event = MissionScenarios.getAllEvents().first()
        val stateWithEmergency = runningState.copy(activeEmergency = event)

        val result = GameEngine.advanceSol(stateWithEmergency)
        // Should not advance sol while emergency is pending
        assertEquals(runningState.currentSol, result.currentSol)
        assertNotNull(result.activeEmergency)
    }

    @Test
    fun testHardwareUpgradesRecoveryEfficiencies() {
        val config = MissionConfig(crewSize = 4)
        val rawState = GameEngine.initializeMission(config)

        // Install Sabatier and Closed-Loop Water upgrades
        val upgrades = MissionScenarios.getDefaultUpgrades()
        val moxieUpgrade = upgrades.first { it.id == "MOXIE_ELECTROLYSIS" }
        val waterUpgrade = upgrades.first { it.id == "CLOSED_LOOP_WATER" }

        val (stateWithMoxie, _) = GameEngine.applyUpgrade(rawState, moxieUpgrade)
        val (stateWithBoth, _) = GameEngine.applyUpgrade(stateWithMoxie, waterUpgrade)

        // Advance 1 sol for raw vs upgraded state
        val rawNext = GameEngine.advanceSol(rawState)
        val upgradedNext = GameEngine.advanceSol(stateWithBoth)

        // Upgraded state should have lost significantly less oxygen and water
        val rawO2Lost = rawState.oxygen - rawNext.oxygen
        val upgradedO2Lost = stateWithBoth.oxygen - upgradedNext.oxygen
        assertTrue("Upgraded O2 loss ($upgradedO2Lost) should be less than raw ($rawO2Lost)", upgradedO2Lost < rawO2Lost)

        val rawWaterLost = rawState.water - rawNext.water
        val upgradedWaterLost = stateWithBoth.water - upgradedNext.water
        assertTrue("Upgraded water loss ($upgradedWaterLost) should be less than raw ($rawWaterLost)", upgradedWaterLost < rawWaterLost)

        assertNotNull(upgradedNext.lastResourceDelta)
        assertTrue(upgradedNext.lastResourceDelta!!.oxygenRecovered > 0f)
        assertTrue(upgradedNext.lastResourceDelta!!.waterRecovered > 0f)
    }

    @Test
    fun testUpgradePurchaseValidationAndIdempotency() {
        val config = MissionConfig(difficulty = Difficulty.STANDARD)
        val state = GameEngine.initializeMission(config)
        val upgrades = MissionScenarios.getDefaultUpgrades()
        val solarUpgrade = upgrades.first { it.id == "SOLAR_ARRAY" }

        // 1. Success purchase
        val (state1, success1) = GameEngine.applyUpgrade(state, solarUpgrade)
        assertTrue(success1)
        assertEquals(state.credits - solarUpgrade.costCredits, state1.credits)
        assertEquals(1, state1.installedUpgrades[solarUpgrade.id])

        // 2. Cannot purchase with insufficient credits
        val poorState = state1.copy(credits = 50)
        val (statePoor, successPoor) = GameEngine.applyUpgrade(poorState, solarUpgrade)
        assertFalse(successPoor)
        assertEquals(50, statePoor.credits)

        // 3. Cannot purchase beyond max tier
        val maxedState = state1.copy(
            credits = 5000,
            installedUpgrades = mapOf(solarUpgrade.id to solarUpgrade.maxPurchases)
        )
        val (stateMaxed, successMaxed) = GameEngine.applyUpgrade(maxedState, solarUpgrade)
        assertFalse(successMaxed)
    }

    @Test
    fun testFailureAnalysisDiagnosesAllKeyCauses() {
        val config = MissionConfig()
        val baseState = GameEngine.initializeMission(config)

        // Dehydration failure
        val dehydratedState = baseState.copy(water = 0.5f, currentSol = 5)
        val failedDehydration = GameEngine.advanceSol(dehydratedState)
        assertEquals(MissionStatus.FAILED, failedDehydration.status)
        assertEquals("Potable Water (H₂O)", failedDehydration.failureAnalysis?.exhaustedResource)

        // Hull Integrity failure
        val brokenHabState = baseState.copy(habitatIntegrity = 0.5f, currentSol = 8)
        val failedHab = GameEngine.advanceSol(brokenHabState)
        assertEquals(MissionStatus.FAILED, failedHab.status)
        assertEquals("Habitat Structural Integrity", failedHab.failureAnalysis?.exhaustedResource)

        // Asphyxiation failure
        val hypoxicState = baseState.copy(oxygen = 0.2f, currentSol = 3)
        val failedHypoxic = GameEngine.advanceSol(hypoxicState)
        assertEquals(MissionStatus.FAILED, failedHypoxic.status)
        assertEquals("Oxygen (O₂)", failedHypoxic.failureAnalysis?.exhaustedResource)
    }

    @Test
    fun testResetMissionReturnsCleanStartingState() {
        val config = MissionConfig(destination = Destination.MARS, crewSize = 6)
        val state1 = GameEngine.initializeMission(config)
        val advanced = GameEngine.advanceSol(state1)

        val reset = GameEngine.resetMission(config)
        assertEquals(1, reset.currentSol)
        assertEquals(state1.oxygen, reset.oxygen, 0.001f)
        assertEquals(state1.water, reset.water, 0.001f)
        assertEquals(state1.food, reset.food, 0.001f)
        assertEquals(state1.credits, reset.credits)
        assertEquals(MissionStatus.IN_PROGRESS, reset.status)
    }
}
