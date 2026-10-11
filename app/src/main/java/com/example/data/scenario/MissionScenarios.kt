package com.example.data.scenario

import com.example.data.model.*

object MissionScenarios {

    fun getDefaultUpgrades(): List<UpgradeItem> {
        return listOf(
            UpgradeItem(
                id = "SOLAR_ARRAY",
                name = "High-Efficiency Photovoltaic Wing",
                category = UpgradeCategory.POWER,
                costCredits = 320,
                description = "Triple-junction gallium arsenide solar array with 32% optical efficiency.",
                benefitText = "+14 kW daily power generation",
                tradeOffText = "Susceptible to dust accumulation (Mars) and orbital shadows (Moon)",
                scienceNote = "NASA's InSight and Opportunity missions demonstrated how atmospheric dust alters solar power; newer space arrays use anti-static electrodynamic dust shields.",
                iconName = "wb_sunny",
                solarBoostKW = 14f,
                powerDemandKW = 0f,
                maxPurchases = 4
            ),
            UpgradeItem(
                id = "BATTERY_BANK",
                name = "Lithium-Titanate Energy Storage Rack",
                category = UpgradeCategory.POWER,
                costCredits = 280,
                description = "High-cycle deep discharge battery rack capable of surviving -40°C thermal extremes.",
                benefitText = "+45 kWh maximum storage capacity, immediately charges +25 kWh",
                tradeOffText = "Adds thermal management load (+1 kW standby demand)",
                scienceNote = "Spacecraft batteries undergo thousands of thermal cycles. Lithium-titanate chemistry prevents dendrite growth in deep-space conditions.",
                iconName = "battery_charging_full",
                batteryCapacityBoostKWh = 45f,
                powerDemandKW = 1f,
                maxPurchases = 3
            ),
            UpgradeItem(
                id = "MOXIE_ELECTROLYSIS",
                name = "Sabatier & MOXIE Oxygen Recycler",
                category = UpgradeCategory.LIFE_SUPPORT,
                costCredits = 380,
                description = "Combines solid oxide electrolysis with Sabatier catalytic CO₂ reduction to recover breathable O₂.",
                benefitText = "Reduces crew daily oxygen consumption by 45%",
                tradeOffText = "Requires steady high temperature (+3 kW electrical draw)",
                scienceNote = "NASA's MOXIE experiment on Mars proved that solid-oxide electrolysis can extract high-purity O₂ from atmospheric CO₂ at 800°C.",
                iconName = "air",
                oxygenRecyclingRateBoost = 0.45f,
                powerDemandKW = 3f,
                maxPurchases = 2
            ),
            UpgradeItem(
                id = "CLOSED_LOOP_WATER",
                name = "Vapor Compression Water Recovery ECLSS",
                category = UpgradeCategory.LIFE_SUPPORT,
                costCredits = 360,
                description = "Rotary distillation system reclaiming 98% of graywater, humidity, and crew urine into potable water.",
                benefitText = "Reduces crew daily water consumption by 55%",
                tradeOffText = "Requires periodic filter flushing (+2 kW electrical draw)",
                scienceNote = "On the International Space Station, the ECLSS Water Processor achieves 98% water recovery, turning moisture and sweat back into certified drinking water.",
                iconName = "water_drop",
                waterRecoveryRateBoost = 0.55f,
                powerDemandKW = 2f,
                maxPurchases = 2
            ),
            UpgradeItem(
                id = "HYDROPONIC_GREENHOUSE",
                name = "Aeroponic Growth Chamber",
                category = UpgradeCategory.FOOD_BIO,
                costCredits = 420,
                description = "Pressurized automated plant growth module with full-spectrum LED lighting and nutrient misting.",
                benefitText = "+7% food yield per Sol, boosts crew mental health (+1%/Sol)",
                tradeOffText = "Consumes 3% water per Sol and 4 kW electrical power",
                scienceNote = "NASA's Veggie experiment and Advanced Plant Habitat on the ISS evaluate leafy greens and dwarf tomatoes for nutritional supplementation on deep space voyages.",
                iconName = "local_florist",
                foodGrowthRateBoost = 7f,
                powerDemandKW = 4f,
                maxPurchases = 3
            ),
            UpgradeItem(
                id = "REGOLITH_SHIELD",
                name = "Sintered Regolith Radiation Shield",
                category = UpgradeCategory.HABITAT,
                costCredits = 340,
                description = "Robotic 3D-printed interlocking protective shell made of fused planetary soil.",
                benefitText = "Cuts radiation risk by 40%, adds +15% Habitat Integrity",
                tradeOffText = "Heavy installation footprint, requires 1 Sol to solidify",
                scienceNote = "NASA and ESA research demonstrates that a 1 to 2-meter layer of lunar or Martian regolith provides shielding equivalent to Earth's atmosphere against galactic cosmic rays.",
                iconName = "shield",
                radiationShieldingBoost = 0.40f,
                habitatIntegrityBoost = 15f,
                maxPurchases = 2
            ),
            UpgradeItem(
                id = "AVIONICS_SPARES",
                name = "Critical Maintenance Spares Depot",
                category = UpgradeCategory.LOGISTICS,
                costCredits = 240,
                description = "Pre-packaged valves, titanium seals, and redundant sensor boards for rapid field repair.",
                benefitText = "Instantly repairs +20% Habitat Integrity; auto-solves mechanical failures",
                tradeOffText = "Consumes storage bay payload volume",
                scienceNote = "Mars missions cannot depend on quick resupply from Earth due to orbital mechanics. Self-sufficient repair depots and 3D spare manufacturing are mandatory.",
                iconName = "build",
                habitatIntegrityBoost = 20f,
                maxPurchases = 3
            ),
            UpgradeItem(
                id = "CREW_MEDBAY",
                name = "Automated Diagnostic & Countermeasure Suite",
                category = UpgradeCategory.LIFE_SUPPORT,
                costCredits = 290,
                description = "Deep-space ultrasound, automated blood analyzer, and resistance exercise countermeasures.",
                benefitText = "Increases daily crew health recovery by +3% and counters low-g atrophy",
                tradeOffText = "Consumes 2 kW baseline power",
                scienceNote = "Human body systems experience fluid shift, bone mineral loss, and cardiovascular deconditioning in fractional gravity without rigorous exercise countermeasures.",
                iconName = "healing",
                powerDemandKW = 2f,
                maxPurchases = 2
            )
        )
    }

    fun getAllEvents(): List<MissionEvent> {
        return listOf(
            MissionEvent(
                id = "EVT_SOLAR_PARTICLE",
                title = "Coronal Mass Ejection: Radiation Alert",
                severity = EventSeverity.CRITICAL,
                targetDestination = null,
                description = "Deep-space observatories report an X-class solar flare directed toward your outpost. High-energy proton flux will breach standard habitat shielding in 2 hours.",
                scienceExplanation = "Coronal Mass Ejections (CMEs) accelerate solar energetic protons to near light speeds. Without Earth's magnetosphere, unshielded surface astronauts would receive lethal radiation doses exceeding 1,000 mSv.",
                choices = listOf(
                    EventChoice(
                        id = "SHELTER_EVAC",
                        title = "Order Emergency Evacuation to Regolith Storm Shelter",
                        description = "Crew huddles inside the water-jacketed bunker. Surface EVAs and science tasks halted for 18 hours.",
                        costCredits = 0,
                        deltaEnergy = -15f,
                        deltaFood = -5f,
                        deltaHealth = 0f,
                        deltaRadiationRisk = -25f,
                        scienceRationale = "Hydrogen-rich materials (like stored water and polyethylene) and dense regolith are the most effective materials to stop penetrating cosmic protons."
                    ),
                    EventChoice(
                        id = "OVERCLOCK_DEFLECTOR",
                        title = "Overclock Active Magnetic Deflector System",
                        description = "Channel maximum battery power into experimental superconducting coils to deflect incoming charged particles.",
                        costCredits = 120,
                        deltaEnergy = -35f,
                        deltaHealth = -4f,
                        deltaRadiationRisk = -15f,
                        scienceRationale = "Active magnetic shielding mimics Earth's Van Allen belts, but requires enormous peak electrical currents."
                    ),
                    EventChoice(
                        id = "CONTINUE_TASKS",
                        title = "Maintain Standard Shifts in Hab with Minimal Shielding",
                        description = "Take anti-radiation medication and prioritize ongoing research tasks for a NASA grant.",
                        costCredits = -180, // gains credits
                        deltaHealth = -22f,
                        deltaRadiationRisk = 30f,
                        scienceRationale = "Severe ionizing radiation damages cellular DNA, causing acute radiation sickness and long-term carcinogenic risk."
                    )
                )
            ),
            MissionEvent(
                id = "EVT_DUST_STORM_MARS",
                title = "Martian Global Dust Storm (Tau > 3.2)",
                severity = EventSeverity.WARNING,
                targetDestination = Destination.MARS,
                description = "A massive dust storm has enveloped the western hemisphere of Mars. Atmospheric opacity has reduced incoming solar irradiance by 65%.",
                scienceExplanation = "Martian dust storms can loft micron-sized particles 50 km high and encircle the entire planet for months, severely restricting solar power as experienced by NASA's Opportunity and InSight rovers.",
                choices = listOf(
                    EventChoice(
                        id = "THROTTLE_SYSTEMS",
                        title = "Engage Low-Power Outpost Conservation Mode",
                        description = "Power down laboratory equipment and dim habitat lighting to preserve battery reserves.",
                        costCredits = 0,
                        deltaEnergy = 10f,
                        deltaHealth = -5f,
                        deltaIntegrity = 0f,
                        scienceRationale = "Conservation profiles allow life-support pumps to operate on critical battery reserves until atmospheric optical depth improves."
                    ),
                    EventChoice(
                        id = "DEPLOY_AUX_FUEL_CELL",
                        title = "Activate Stored Hydrogen-Oxygen Backup Fuel Cell",
                        description = "Burn emergency reactants to maintain full power across the habitat.",
                        costCredits = 150,
                        deltaEnergy = 25f,
                        deltaOxygen = -5f,
                        deltaWater = 8f, // fuel cell creates water byproduct!
                        scienceRationale = "Hydrogen-oxygen fuel cells generate electricity and produce clean potable water as an advantageous chemical byproduct."
                    ),
                    EventChoice(
                        id = "IGNORE_DUST",
                        title = "Run Solar Arrays at Normal Draw & Deplete Batteries",
                        description = "Do not throttle power. Hope the storm clears before batteries hit critical zero.",
                        costCredits = 0,
                        deltaEnergy = -40f,
                        deltaIntegrity = -8f,
                        scienceRationale = "Running batteries below minimum voltage causes irreversible cell degradation and risks total grid blackout."
                    )
                )
            ),
            MissionEvent(
                id = "EVT_LUNAR_LONG_SHADOW",
                title = "Lunar Polar Crater Rim Eclipse",
                severity = EventSeverity.WARNING,
                targetDestination = Destination.MOON,
                description = "Orbital precession has cast the long shadow of Malapert Mountain over your solar farm for the next 36 hours. Temperatures plunge to -180°C.",
                scienceExplanation = "Even at the lunar south pole peaks of eternal light, seasonal shifts cast prolonged shadows. Thermal management is critical as equipment freezes rapidly without heating.",
                choices = listOf(
                    EventChoice(
                        id = "BATTERY_HEATING",
                        title = "Reroute Battery Reserves to Radiator Loop Heaters",
                        description = "Prioritize thermal survival of critical ECLSS plumbing.",
                        costCredits = 0,
                        deltaEnergy = -30f,
                        deltaIntegrity = 5f,
                        scienceRationale = "Electric resistance heaters prevent water and coolant loops from freezing and rupturing expensive habitat conduits."
                    ),
                    EventChoice(
                        id = "RADIOISOTOPE_HEAT",
                        title = "Deploy Radioisotope Heater Unit (RHU) Spare Package",
                        description = "Install passive plutonium-238 decay thermal capsules into the utility core.",
                        costCredits = 180,
                        deltaEnergy = -10f,
                        deltaIntegrity = 12f,
                        scienceRationale = "NASA's Apollo, Curiosity, and Perseverance missions utilized radioisotope decay heat to protect instrumentation from extraterrestrial night chills."
                    )
                )
            ),
            MissionEvent(
                id = "EVT_ECLSS_PUMP_FAILURE",
                title = "Water Recovery Distillation Pump Cavitation",
                severity = EventSeverity.WARNING,
                targetDestination = null,
                description = "The vacuum centrifuge assembly in the water processing loop has seized up due to mineral crystallization. Potable water recycling has stopped.",
                scienceExplanation = "Closed-loop water recycling operates on vacuum rotary distillation. Urine pre-treatment requires precision acid dosing; without it, gypsum and calcium sulfate crystals foul rotating seals.",
                choices = listOf(
                    EventChoice(
                        id = "REPLACE_PUMP",
                        title = "Install New Pre-Treated Pump Unit from Spares",
                        description = "Use replacement parts from logistics inventory to restore 100% recycling immediately.",
                        costCredits = 110,
                        deltaWater = 10f,
                        deltaIntegrity = 8f,
                        scienceRationale = "Modular line-replaceable units (LRUs) allow astronaut crews to swap failed pumps in under 30 minutes."
                    ),
                    EventChoice(
                        id = "MANUAL_DESCALING",
                        title = "Order Crew EVA & Manual Acid Descaling",
                        description = "Crew spends 12 grueling hours flushing the centrifuge with citric acid.",
                        costCredits = 0,
                        deltaHealth = -12f,
                        deltaWater = 0f,
                        deltaOxygen = -5f,
                        scienceRationale = "Manual maintenance saves financial budget but exhausts the crew and consumes extra spacesuit consumables."
                    ),
                    EventChoice(
                        id = "RATION_RESERVES",
                        title = "Bypass Recycler and Rely on Emergency Water Tanks",
                        description = "Do not repair now; drink from unreplenished reserves.",
                        costCredits = 0,
                        deltaWater = -22f,
                        deltaHealth = -4f,
                        scienceRationale = "Without closed-loop recovery, a human crew consumes ~2.5 liters of water per person per day, rapidly depleting storage."
                    )
                )
            ),
            MissionEvent(
                id = "EVT_MICROMETEORITE_LEAK",
                title = "Micrometeorite Puncture in Hab Bulkhead",
                severity = EventSeverity.CRITICAL,
                targetDestination = null,
                description = "Audible alarm! A millimeter-scale particle traveling at 25 km/s has penetrated an outer habitat wall, causing a continuous pressure drop.",
                scienceExplanation = "Planetary bodies without thick atmospheres are bombarded by micro-meteoroids. Hypervelocity impacts create microscopic puncture fissures that vent cabin atmosphere into space.",
                choices = listOf(
                    EventChoice(
                        id = "SEAL_BULKHEAD",
                        title = "Apply Rapid Polymer Vacuum Patch & Internal Sealant",
                        description = "Rapidly clamp an epoxy patch over the breach to stop air loss.",
                        costCredits = 90,
                        deltaOxygen = -8f,
                        deltaIntegrity = 12f,
                        scienceRationale = "Fast-curing silicone and elastomeric patches stop cabin air loss before decompression triggers automatic module isolation."
                    ),
                    EventChoice(
                        id = "SEAL_OFF_MODULE",
                        title = "Isolate Damaged Hab Module Completely",
                        description = "Drop armored airtight hatch doors. Crew living space is restricted.",
                        costCredits = 0,
                        deltaOxygen = -16f,
                        deltaHealth = -8f,
                        deltaIntegrity = -15f,
                        scienceRationale = "Compartmentalization saves the base at the cost of living quarters and structural volume."
                    )
                )
            ),
            MissionEvent(
                id = "EVT_ISRU_BREAKTHROUGH",
                title = "ISRU Discovery: Concentrated Volatiles Detected",
                severity = EventSeverity.INFO,
                targetDestination = null,
                description = "Autonomous prospecting rover sends telemetry: neutron spectrometer detected a shallow deposit of water-ice and volatiles only 400m from base!",
                scienceExplanation = "In-Situ Resource Utilization (ISRU) transforms planetary soils and ice into rocket propellant, drinking water, and oxygen, reducing the need for costly launches from Earth.",
                choices = listOf(
                    EventChoice(
                        id = "MINE_ICE",
                        title = "Deploy Thermal Sublimation Extractor",
                        description = "Bake regolith to vaporize and capture clean water into base reservoirs.",
                        costCredits = -60,
                        deltaEnergy = -15f,
                        deltaWater = 28f,
                        deltaOxygen = 10f,
                        scienceRationale = "Heating cryogenic ice-rich regolith vaporizes water into cold traps where it freezes into high-purity water."
                    ),
                    EventChoice(
                        id = "TRANSMIT_DATA",
                        title = "Transmit High-Resolution Survey Data to Earth",
                        description = "Publish geological findings for international science credits.",
                        costCredits = -220, // earns credits
                        deltaEnergy = -5f,
                        scienceRationale = "High-value resource mapping unlocks scientific grants and funding from Earth space agencies."
                    )
                )
            ),
            MissionEvent(
                id = "EVT_ARTEMIS_RESUPPLY",
                title = "Autonomous Commercial Cargo Lander Touchdown",
                severity = EventSeverity.INFO,
                targetDestination = null,
                description = "A precision lunar/Martian cargo lander has touched down on the auxiliary landing pad. Fresh supplies and spare parts are ready for offloading.",
                scienceExplanation = "NASA's Artemis Commercial Lunar Payload Services (CLPS) program contracts commercial landers to deliver scientific instruments and human logistics regularly.",
                choices = listOf(
                    EventChoice(
                        id = "OFFLOAD_RATIONS",
                        title = "Unload Fresh Hydroponic Seedlings & Gourmet Food Rations",
                        description = "Boost crew spirits with wholesome fresh nutrition.",
                        costCredits = 0,
                        deltaFood = 25f,
                        deltaHealth = 15f,
                        scienceRationale = "Fresh dietary variety prevents astronaut nutritional fatigue and restores immune system resilience."
                    ),
                    EventChoice(
                        id = "OFFLOAD_ECLSS_GASES",
                        title = "Unload Cryogenic Oxygen & Fuel Cell Reactants",
                        description = "Top up primary atmospheric gas storage and energy buffer.",
                        costCredits = 0,
                        deltaOxygen = 30f,
                        deltaEnergy = 25f,
                        scienceRationale = "Cryogenic liquid oxygen (LOX) provides dense breathing gas replenishment without requiring power-hungry extraction."
                    )
                )
            )
        )
    }
}
