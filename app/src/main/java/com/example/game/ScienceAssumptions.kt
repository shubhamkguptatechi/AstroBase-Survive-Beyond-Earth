package com.example.game

data class ScienceAssumption(
    val resourceOrDomain: String,
    val simulatedScale: String,
    val realNasaBaseline: String,
    val educationalRationale: String,
    val sourceCitations: String
)

object ScienceAssumptions {

    val assumptions: List<ScienceAssumption> = listOf(
        ScienceAssumption(
            resourceOrDomain = "Oxygen Consumption & ECLSS",
            simulatedScale = "Normalized 0..100% tank capacity; drains ~1.4% per astronaut per Sol baseline, reduced up to 45% with Sabatier/MOXIE.",
            realNasaBaseline = "~0.84 kg of O₂ consumed per astronaut per day at 21 kPa sea-level partial pressure.",
            educationalRationale = "Normalized percentages allow learners to intuitively visualize reserve depletion and life-support margin without needing complex stoichiometry calculations.",
            sourceCitations = "NASA Human Integration Design Handbook (HIDH) SP-2010-3407; NASA ECLSS Reference Architecture."
        ),
        ScienceAssumption(
            resourceOrDomain = "Water Recycling & Distillation",
            simulatedScale = "Normalized 0..100% reservoir; drains ~1.8% per astronaut per Sol, reduced by 55% with vapor compression recovery.",
            realNasaBaseline = "~2.5 to 3.0 liters of potable water required per person-day for hydration and rehydration; ISS ECLSS recovers ~98% of humidity/urine.",
            educationalRationale = "Demonstrates the necessity of high closed-loop recovery efficiencies for deep space outposts without constant Earth resupply flights.",
            sourceCitations = "ISS ECLSS Water Processor Assembly (WPA) Technical Reports; NASA Tech Briefs."
        ),
        ScienceAssumption(
            resourceOrDomain = "Food & Crop Production",
            simulatedScale = "Normalized 0..100% shelf-stable pantry; drains ~2.0% per person per Sol, supplemented by +7%/Sol per Aeroponic Chamber.",
            realNasaBaseline = "~2000 to 2800 kcal (approx. 0.62 kg dry mass) per astronaut per day; supplemental microgreens from Veggie/Advanced Plant Habitat.",
            educationalRationale = "Highlights the trade-off of biological life support: crops regenerate crew nutrition and morale but require electrical power and water inputs.",
            sourceCitations = "NASA Advanced Plant Habitat (APH); Kennedy Space Center Space Crop Production Research."
        ),
        ScienceAssumption(
            resourceOrDomain = "Electrical Power & Solar Constant",
            simulatedScale = "Moon: 32 kW baseline array generation; Mars: 26 kW baseline array generation with dust attenuation factors.",
            realNasaBaseline = "Solar constant at Earth/Moon (1.0 AU) is ~1361 W/m²; at Mars (1.52 AU) it drops to ~590 W/m² (~43%).",
            educationalRationale = "Accurately conveys the inverse-square law of solar radiation while modeling battery storage buffering for night/dust survival.",
            sourceCitations = "NASA InSight / Opportunity Power Subsystem Telemetry; Artemis Kilopower & Surface Power Architecture."
        ),
        ScienceAssumption(
            resourceOrDomain = "Space Radiation & Regolith Shielding",
            simulatedScale = "Normalized 0..100% ambient risk score; cuts by 40% with 3D-sintered regolith shielding.",
            realNasaBaseline = "Galactic Cosmic Rays deliver ~250-400 mSv/year on unshielded Mars/Moon surfaces; Solar Particle Events can spike to lethal > 1000 mSv.",
            educationalRationale = "Teaches students that planetary soil (in-situ regolith) is an essential radiation barrier against energetic cosmic nuclei.",
            sourceCitations = "NASA Space Radiation Laboratory (NSRL); MSL RAD Instrument Publications (Hassler et al., Science)."
        ),
        ScienceAssumption(
            resourceOrDomain = "Habitat Structural Integrity",
            simulatedScale = "Normalized 0..100% durability; decays 1.2%/Sol from thermal fatigue and micrometeoroids; leaks air below 25%.",
            realNasaBaseline = "Multilayer Whipple bumpers and ceramic aerogels withstand micrometeoroid impacts at 15-25 km/s; thermal cycles swing 300°C+.",
            educationalRationale = "Shows that extraterrestrial habitats require continuous preventative maintenance rather than passive occupation.",
            sourceCitations = "NASA Orbital Debris Program Office; Apollo Thermal Protection Telemetry."
        )
    )
}
