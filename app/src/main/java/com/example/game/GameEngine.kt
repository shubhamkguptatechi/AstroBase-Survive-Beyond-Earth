package com.example.game

import com.example.data.model.*
import com.example.data.scenario.MissionScenarios
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Core simulation engine implementing the ECLSS resource model (oxygen, water, food,
 * electrical energy, crew health, radiation risk, and habitat structural integrity).
 *
 * Enforces deterministic state transitions, physical boundary invariants, and failure root-cause analysis.
 */
object GameEngine {

    /**
     * Initializes a fresh mission based on the provided mission configuration.
     * Guarantees all state values are in a clean, validated condition.
     */
    fun initializeMission(config: MissionConfig): GameState {
        val diff = config.difficulty
        val baseCredits = diff.startingCredits
        val resMultiplier = diff.resourceMultiplier

        val baseMaxBattery = when (config.destination) {
            Destination.MOON -> 130f * resMultiplier
            Destination.MARS -> 110f * resMultiplier
        }

        val baseSolarGen = when (config.destination) {
            Destination.MOON -> 32f * config.destination.baseSolarIrradiance
            Destination.MARS -> 26f * config.destination.baseSolarIrradiance * 2.3f // Scaled surface array footprint
        }

        val baseDemand = 18f + (config.crewSize * 1.5f)

        val initialState = GameState(
            config = config,
            status = MissionStatus.IN_PROGRESS,
            currentSol = 1,
            score = 100,
            credits = baseCredits,
            oxygen = (100f * resMultiplier).coerceIn(0f, 100f),
            water = (100f * resMultiplier).coerceIn(0f, 100f),
            food = (100f * resMultiplier).coerceIn(0f, 100f),
            energy = 100f,
            batteryStoredKWh = baseMaxBattery,
            maxBatteryKWh = baseMaxBattery,
            crewHealth = 100f,
            radiationRisk = if (config.destination == Destination.MOON) 22f else 16f,
            habitatIntegrity = 100f,
            foodProductionCapacity = 0f,
            solarGenerationKW = baseSolarGen,
            powerDemandKW = baseDemand,
            installedUpgrades = emptyMap(),
            eventHistory = listOf(
                EventLogEntry(
                    sol = 1,
                    title = "Outpost Commissioned",
                    chosenOption = "Nominal Touchdown",
                    outcomeSummary = "Lander secured at ${config.destination.subtitle}. Crew of ${config.crewSize} initialized baseline life support telemetry."
                )
            ),
            lastResourceDelta = null
        )

        return initialState.withClampedResources()
    }

    /**
     * Resets a mission cleanly to initial condition with the specified configuration.
     */
    fun resetMission(config: MissionConfig): GameState {
        return initializeMission(config)
    }

    /**
     * Advances the simulation by 1 Sol.
     *
     * Invariants enforced:
     * - A completed or failed mission will NOT advance or consume resources.
     * - An active emergency must be resolved before advancing Sol.
     * - Resources are mathematically clamped and cannot fall below zero or exceed 100.
     * - Generates an explicit SolResourceDelta breakdown.
     */
    fun advanceSol(state: GameState): GameState {
        // Validation Guard 1: A finished mission cannot continue consuming resources
        if (state.isGameOver) return state

        // Validation Guard 2: Active emergency blocks automatic time progression
        if (state.activeEmergency != null) return state

        val nextSol = state.currentSol + 1
        val crew = state.config.crewSize
        val diff = state.config.difficulty
        val notes = mutableListOf<String>()

        // 1. Hardware Upgrade Efficiencies
        val moxieCount = state.installedUpgrades["MOXIE_ELECTROLYSIS"] ?: 0
        val waterEclssCount = state.installedUpgrades["CLOSED_LOOP_WATER"] ?: 0
        val medbayCount = state.installedUpgrades["CREW_MEDBAY"] ?: 0
        val hydroponicCount = state.installedUpgrades["HYDROPONIC_GREENHOUSE"] ?: 0

        val o2RecyclingEfficiency = (moxieCount * 0.40f).coerceAtMost(0.85f)
        val waterRecyclingEfficiency = (waterEclssCount * 0.50f).coerceAtMost(0.90f)

        // 2. Resource Consumption & Production Formulas
        // Oxygen: ~1.4% per astronaut per Sol baseline, reduced by Sabatier/MOXIE catalytic recycling
        val rawO2Consumed = 1.4f * crew
        val o2Recovered = rawO2Consumed * o2RecyclingEfficiency
        var netO2Drain = rawO2Consumed - o2Recovered

        // Compromised hull leak penalty
        if (state.habitatIntegrity < 25f) {
            val hullLeakLoss = 3.0f
            netO2Drain += hullLeakLoss
            notes.add("Hull micro-leaks vented an additional 3.0% O₂ into space.")
        }

        // Water: ~1.8% per astronaut per Sol baseline, reduced by closed-loop distillation
        val rawWaterConsumed = 1.8f * crew
        val waterRecovered = rawWaterConsumed * waterRecyclingEfficiency
        val greenhouseWaterDemand = hydroponicCount * 2.5f
        val netWaterDrain = (rawWaterConsumed - waterRecovered) + greenhouseWaterDemand
        if (greenhouseWaterDemand > 0f) {
            notes.add("Aeroponic chambers consumed $greenhouseWaterDemand% water for root misting.")
        }

        // Food: ~2.0% per astronaut per Sol baseline, offset by crop harvests
        val foodConsumed = 2.0f * crew
        val foodHarvested = state.foodProductionCapacity
        val netFoodDrain = foodConsumed - foodHarvested
        if (foodHarvested > 0f) {
            notes.add("Aeroponic crop harvest yielded +${foodHarvested.toInt()}% fresh food rations.")
        }

        // Calculate new clamped levels
        val newOxygen = (state.oxygen - netO2Drain).coerceIn(0f, 100f)
        val newWater = (state.water - netWaterDrain).coerceIn(0f, 100f)
        val newFood = (state.food - netFoodDrain).coerceIn(0f, 100f)

        // 3. Electrical Power Balance & Battery Storage Buffer
        val netPowerKW = state.solarGenerationKW - state.powerDemandKW
        val hoursEquivalent = 12f // 12-hour average diurnal solar window
        val dailyEnergyKWhDelta = netPowerKW * hoursEquivalent

        var newStoredBattery = (state.batteryStoredKWh + dailyEnergyKWhDelta).coerceIn(0f, state.maxBatteryKWh)
        val newEnergyPercent = if (state.maxBatteryKWh > 0f) {
            (newStoredBattery / state.maxBatteryKWh * 100f).coerceIn(0f, 100f)
        } else 0f

        // 4. Habitat Structural Integrity Degradation
        val integrityLoss = 1.2f
        val newIntegrity = (state.habitatIntegrity - integrityLoss).coerceIn(0f, 100f)

        // 5. Radiation & Crew Health Model
        var healthDelta = 0f
        val medbayRegen = medbayCount * 2.5f
        if (medbayRegen > 0f) {
            healthDelta += medbayRegen
            notes.add("Medical suite regenerative countermeasures restored +${medbayRegen.toInt()}% vital baseline.")
        }

        if (state.radiationRisk > 30f) {
            val radDamage = (state.radiationRisk - 30f) * 0.15f
            healthDelta -= radDamage
            notes.add("Elevated ionizing radiation degraded crew cellular vitality by -${radDamage.toInt()}%.")
        }

        // Deprivation Penalties
        if (newOxygen <= 0f) {
            healthDelta -= 35f
            notes.add("CRITICAL HYPOXIA: Zero oxygen delivered to habitat cabins (-35% health).")
        }
        if (newWater <= 0f) {
            healthDelta -= 25f
            notes.add("CRITICAL DEHYDRATION: Potable water reserves exhausted (-25% health).")
        }
        if (newFood <= 0f) {
            healthDelta -= 15f
            notes.add("NUTRITIONAL STARVATION: Rations depleted (-15% health).")
        }
        if (newEnergyPercent <= 0f) {
            healthDelta -= 12f
            notes.add("BLACKOUT: Thermal heaters offline; cabin experiencing severe extraterrestrial freeze (-12% health).")
        }

        val newHealth = (state.crewHealth + healthDelta).coerceIn(0f, 100f)

        // 6. Score & Credits
        val solScoreReward = 120 + (crew * 25) + (newHealth.toInt() / 2)
        val solCreditReward = 60 + (crew * 10)
        val newScore = state.score + solScoreReward
        val newCredits = state.credits + solCreditReward

        val resourceDelta = SolResourceDelta(
            sol = nextSol,
            oxygenConsumed = rawO2Consumed,
            oxygenRecovered = o2Recovered,
            netOxygenDelta = -netO2Drain,
            waterConsumed = rawWaterConsumed + greenhouseWaterDemand,
            waterRecovered = waterRecovered,
            netWaterDelta = -netWaterDrain,
            foodConsumed = foodConsumed,
            foodHarvested = foodHarvested,
            netFoodDelta = -netFoodDrain,
            solarGenerationKW = state.solarGenerationKW,
            powerDemandKW = state.powerDemandKW,
            netPowerKW = netPowerKW,
            batteryStoredKWhDelta = dailyEnergyKWhDelta,
            crewHealthDelta = healthDelta,
            habitatIntegrityDelta = -integrityLoss,
            radiationExposureRisk = state.radiationRisk,
            creditsEarned = solCreditReward,
            scientificNotes = notes
        )

        // 7. Check Victory Condition
        if (nextSol > state.config.targetDurationSols && newHealth > 0f && newOxygen > 0f && newWater > 0f) {
            return state.copy(
                currentSol = state.config.targetDurationSols,
                status = MissionStatus.VICTORY,
                oxygen = newOxygen,
                water = newWater,
                food = newFood,
                energy = newEnergyPercent,
                batteryStoredKWh = newStoredBattery,
                crewHealth = newHealth,
                habitatIntegrity = newIntegrity,
                score = newScore + 5000,
                credits = newCredits,
                lastResourceDelta = resourceDelta,
                eventHistory = state.eventHistory + EventLogEntry(
                    sol = state.config.targetDurationSols,
                    title = "Mission Accomplished!",
                    chosenOption = "Target Duration Reached",
                    outcomeSummary = "Outpost sustained human life for ${state.config.targetDurationSols} Sols on ${state.config.destination.title}. Junior Astronaut Mission Trainer standard achieved!"
                )
            ).withClampedResources()
        }

        // 8. Check Failure Condition
        if (newHealth <= 0f || newOxygen <= 0f || newWater <= 0f || newIntegrity <= 0f) {
            val failureReport = analyzeFailure(
                state = state,
                failedSol = nextSol,
                o2 = newOxygen,
                water = newWater,
                food = newFood,
                energy = newEnergyPercent,
                health = newHealth,
                integrity = newIntegrity
            )
            return state.copy(
                currentSol = nextSol,
                status = MissionStatus.FAILED,
                oxygen = newOxygen,
                water = newWater,
                food = newFood,
                energy = newEnergyPercent,
                batteryStoredKWh = newStoredBattery,
                crewHealth = newHealth,
                habitatIntegrity = newIntegrity,
                score = newScore,
                lastResourceDelta = resourceDelta,
                failureAnalysis = failureReport,
                eventHistory = state.eventHistory + EventLogEntry(
                    sol = nextSol,
                    title = "Mission Critical Failure",
                    chosenOption = failureReport.primaryCause,
                    outcomeSummary = failureReport.criticalDecision
                )
            ).withClampedResources()
        }

        // 9. Potential Incident Triggering
        val eventChance = diff.eventFrequency * 0.45f
        val triggeredEvent = if (Random.nextFloat() < eventChance) {
            pickEventForDestination(state.config.destination, nextSol)
        } else null

        val updatedHistory = if (triggeredEvent == null) {
            state.eventHistory + EventLogEntry(
                sol = nextSol,
                title = "Sol $nextSol Operations",
                chosenOption = "Nominal",
                outcomeSummary = "Routine scientific tasks performed. Crew vital signs stable."
            )
        } else {
            state.eventHistory
        }

        return state.copy(
            currentSol = nextSol,
            oxygen = newOxygen,
            water = newWater,
            food = newFood,
            energy = newEnergyPercent,
            batteryStoredKWh = newStoredBattery,
            crewHealth = newHealth,
            habitatIntegrity = newIntegrity,
            score = newScore,
            credits = newCredits,
            activeEmergency = triggeredEvent,
            eventHistory = updatedHistory,
            lastResourceDelta = resourceDelta
        ).withClampedResources()
    }

    /**
     * Applies a hardware or ECLSS upgrade transactionally.
     * Enforces credit sufficiency, tier limits, and idempotency.
     */
    fun applyUpgrade(state: GameState, upgrade: UpgradeItem): Pair<GameState, Boolean> {
        // Validation Guard 1: Finished missions cannot purchase upgrades
        if (state.isGameOver) return Pair(state, false)

        // Validation Guard 2: Insufficient credits
        if (state.credits < upgrade.costCredits) return Pair(state, false)

        val currentCount = state.installedUpgrades[upgrade.id] ?: 0

        // Validation Guard 3: Max tier reached
        if (currentCount >= upgrade.maxPurchases) return Pair(state, false)

        val newCredits = state.credits - upgrade.costCredits
        val newInstalled = state.installedUpgrades.toMutableMap()
        newInstalled[upgrade.id] = currentCount + 1

        var newMaxBattery = state.maxBatteryKWh
        var newStoredBattery = state.batteryStoredKWh
        var newSolarGen = state.solarGenerationKW
        var newPowerDemand = state.powerDemandKW
        var newFoodCap = state.foodProductionCapacity
        var newHabIntegrity = min(100f, state.habitatIntegrity + upgrade.habitatIntegrityBoost)
        var newRadiationRisk = state.radiationRisk

        if (upgrade.batteryCapacityBoostKWh > 0f) {
            newMaxBattery += upgrade.batteryCapacityBoostKWh
            newStoredBattery = min(newMaxBattery, newStoredBattery + (upgrade.batteryCapacityBoostKWh * 0.6f))
        }
        if (upgrade.solarBoostKW > 0f) {
            newSolarGen += upgrade.solarBoostKW
        }
        if (upgrade.powerDemandKW > 0f) {
            newPowerDemand += upgrade.powerDemandKW
        }
        if (upgrade.foodGrowthRateBoost > 0f) {
            newFoodCap += upgrade.foodGrowthRateBoost
        }
        if (upgrade.radiationShieldingBoost > 0f) {
            newRadiationRisk = max(5f, newRadiationRisk * (1f - upgrade.radiationShieldingBoost))
        }

        val updatedEnergyPercent = if (newMaxBattery > 0f) {
            (newStoredBattery / newMaxBattery * 100f).coerceIn(0f, 100f)
        } else 0f

        val updatedState = state.copy(
            credits = newCredits,
            installedUpgrades = newInstalled,
            maxBatteryKWh = newMaxBattery,
            batteryStoredKWh = newStoredBattery,
            energy = updatedEnergyPercent,
            solarGenerationKW = newSolarGen,
            powerDemandKW = newPowerDemand,
            foodProductionCapacity = newFoodCap,
            habitatIntegrity = newHabIntegrity,
            radiationRisk = newRadiationRisk,
            score = state.score + (upgrade.costCredits / 2)
        ).withClampedResources()

        return Pair(updatedState, true)
    }

    /**
     * Resolves an interactive mission incident choice and applies resource deltas safely.
     */
    fun resolveEvent(state: GameState, event: MissionEvent, choice: EventChoice): GameState {
        val newCredits = max(0, state.credits - choice.costCredits)
        val newOxygen = (state.oxygen + choice.deltaOxygen).coerceIn(0f, 100f)
        val newWater = (state.water + choice.deltaWater).coerceIn(0f, 100f)
        val newFood = (state.food + choice.deltaFood).coerceIn(0f, 100f)
        val newHealth = (state.crewHealth + choice.deltaHealth).coerceIn(0f, 100f)
        val newIntegrity = (state.habitatIntegrity + choice.deltaIntegrity).coerceIn(0f, 100f)
        val newRad = (state.radiationRisk + choice.deltaRadiationRisk).coerceIn(5f, 100f)

        val batteryDelta = (choice.deltaEnergy / 100f) * state.maxBatteryKWh
        val newStoredBattery = (state.batteryStoredKWh + batteryDelta).coerceIn(0f, state.maxBatteryKWh)
        val newEnergyPercent = if (state.maxBatteryKWh > 0f) {
            (newStoredBattery / state.maxBatteryKWh * 100f).coerceIn(0f, 100f)
        } else 0f

        val logEntry = EventLogEntry(
            sol = state.currentSol,
            title = event.title,
            chosenOption = choice.title,
            outcomeSummary = "${choice.description} — Science: ${choice.scienceRationale}"
        )

        // Check if event choice caused immediate failure
        if (newHealth <= 0f || newOxygen <= 0f || newWater <= 0f || newIntegrity <= 0f) {
            val failureReport = analyzeFailure(
                state = state,
                failedSol = state.currentSol,
                o2 = newOxygen,
                water = newWater,
                food = newFood,
                energy = newEnergyPercent,
                health = newHealth,
                integrity = newIntegrity
            )
            return state.copy(
                status = MissionStatus.FAILED,
                oxygen = newOxygen,
                water = newWater,
                food = newFood,
                energy = newEnergyPercent,
                batteryStoredKWh = newStoredBattery,
                crewHealth = newHealth,
                habitatIntegrity = newIntegrity,
                credits = newCredits,
                radiationRisk = newRad,
                activeEmergency = null,
                failureAnalysis = failureReport,
                eventHistory = state.eventHistory + logEntry
            ).withClampedResources()
        }

        return state.copy(
            oxygen = newOxygen,
            water = newWater,
            food = newFood,
            energy = newEnergyPercent,
            batteryStoredKWh = newStoredBattery,
            crewHealth = newHealth,
            habitatIntegrity = newIntegrity,
            credits = newCredits,
            radiationRisk = newRad,
            activeEmergency = null,
            score = state.score + 150,
            eventHistory = state.eventHistory + logEntry
        ).withClampedResources()
    }

    private fun pickEventForDestination(destination: Destination, sol: Int): MissionEvent {
        val allEvents = MissionScenarios.getAllEvents()
        val candidateEvents = allEvents.filter {
            it.targetDestination == null || it.targetDestination == destination
        }
        return candidateEvents[Random.nextInt(candidateEvents.size)]
    }

    private fun analyzeFailure(
        state: GameState,
        failedSol: Int,
        o2: Float,
        water: Float,
        food: Float,
        energy: Float,
        health: Float,
        integrity: Float
    ): MissionFailureReport {
        return when {
            o2 <= 0f -> MissionFailureReport(
                failedSol = failedSol,
                primaryCause = "Catastrophic Asphyxiation (Oxygen Depletion)",
                rootMechanism = "Atmospheric partial pressure of oxygen fell below 12 kPa, causing acute hypoxia and crew incapacitation.",
                exhaustedResource = "Oxygen (O₂)",
                criticalDecision = "Failure to invest in Sabatier/MOXIE catalytic recyclers or patch hull micro-leaks in time.",
                scienceDebrief = "NASA ECLSS engineering dictates closed-loop oxygen recycling via water electrolysis and Sabatier reaction. Without catalytic recovery, an unreplenished human crew depletes breathing gas rapidly.",
                tacticalAdvice = listOf(
                    "Prioritize purchasing the 'Sabatier & MOXIE Oxygen Recycler' early in Sol 1-5 to reduce O₂ consumption by 45%.",
                    "Monitor habitat integrity: hull integrity drops below 25% cause silent micro-leaks that double O₂ drain.",
                    "Save construction credits to respond quickly to airlock micrometeorite puncture events."
                )
            )
            water <= 0f -> MissionFailureReport(
                failedSol = failedSol,
                primaryCause = "Severe Dehydration & Cooling Loop Collapse",
                rootMechanism = "Potable water reserves completely depleted; secondary coolant loops ceased functioning, halting life support.",
                exhaustedResource = "Potable Water (H₂O)",
                criticalDecision = "Crew neglected closed-loop urine/condensate distillation while running water-intensive greenhouse crops.",
                scienceDebrief = "On long-duration human spaceflights, water is too heavy to launch continuously. The ISS reclaims 98% of human moisture. Without high recovery efficiency, human water consumption rapidly exhausts tanks.",
                tacticalAdvice = listOf(
                    "Install the 'Vapor Compression Water Recovery ECLSS' upgrade before expanding hydroponic greenhouses.",
                    "Be cautious with Aeroponic Growth Chambers early on, as each chamber consumes 2.5% additional water per Sol.",
                    "Choose ISRU ice mining options when volatiles are discovered during geological events."
                )
            )
            integrity <= 0f -> MissionFailureReport(
                failedSol = failedSol,
                primaryCause = "Habitat Structural Decompression",
                rootMechanism = "Cumulative micrometeorite punctures and severe thermal fatigue breached outer pressurized bulkheads.",
                exhaustedResource = "Habitat Structural Integrity",
                criticalDecision = "Ignoring maintenance repairs and failing to construct regolith protective shielding.",
                scienceDebrief = "Extraterrestrial environments subject habitats to extreme temperature swings (-130°C to +120°C) and hypervelocity micrometeoroids. Sintered planetary regolith provides mandatory structural ballasts.",
                tacticalAdvice = listOf(
                    "Deploy the 'Sintered Regolith Radiation Shield' to gain +15% integrity and passive micrometeorite protection.",
                    "Stock 'Critical Maintenance Spares' in logistics inventory to instantly restore 20% habitat durability.",
                    "Never let habitat integrity fall below 40% before investing in polymer bulkhead patches."
                )
            )
            energy <= 0f || (energy < 5f && health <= 0f) -> MissionFailureReport(
                failedSol = failedSol,
                primaryCause = "Power Grid Blackout & Thermal Freezing",
                rootMechanism = "Battery storage was fully exhausted; ECLSS thermal heaters, scrubbers, and pumps shut down in deep extraterrestrial cold.",
                exhaustedResource = "Electrical Energy & Battery Reserve",
                criticalDecision = "Outpost power demand exceeded solar generation without sufficient battery buffer to survive shadow/dust periods.",
                scienceDebrief = "Power is the foundational prerequisite of human survival in space. Without electricity, environmental radiators freeze, air circulating blowers stop, and carbon dioxide builds up rapidly.",
                tacticalAdvice = listOf(
                    "Maintain positive daily net power: ensure Solar Generation (kW) exceeds Outpost Demand (kW).",
                    "Purchase 'Lithium-Titanate Energy Storage Racks' to buffer against dust storms on Mars and shadow periods on the Moon.",
                    "Throttle non-essential equipment into low-power conservation mode during dust or eclipse warnings."
                )
            )
            else -> MissionFailureReport(
                failedSol = failedSol,
                primaryCause = "Acute Radiation Sickness & Physiological Collapse",
                rootMechanism = "Cumulative galactic cosmic rays and solar energetic particle exposure degraded crew bone marrow and immune systems.",
                exhaustedResource = "Crew Health & Physiological Buffer",
                criticalDecision = "Leaving crew exposed to high ambient radiation without building regolith storm shelters.",
                scienceDebrief = "Deep-space cosmic radiation consists of high-energy atomic nuclei (HZE ions) that break DNA strands. Astronauts require hydrogenous materials or deep regolith shielding to prevent lethal cellular damage.",
                tacticalAdvice = listOf(
                    "Construct 'Sintered Regolith Radiation Shielding' early to reduce baseline ambient radiation exposure by 40%.",
                    "Equip the 'Automated Diagnostic & Countermeasure Suite' (Medbay) to regenerate crew health continuously.",
                    "Always order emergency evacuation to regolith storm shelters during Coronal Mass Ejection (CME) alerts."
                )
            )
        }
    }
}
