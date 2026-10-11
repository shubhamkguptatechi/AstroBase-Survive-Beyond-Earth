package com.example.data.model

enum class Destination(
    val title: String,
    val subtitle: String,
    val gravity: String,
    val atmosphere: String,
    val surfaceTemp: String,
    val dayNightCycle: String,
    val radiationHazard: String,
    val commDelay: String,
    val waterAvailability: String,
    val baseSolarIrradiance: Float, // W/m2 relative factor
    val scienceFact: String,
    val nasaSourceUrl: String
) {
    MOON(
        title = "Moon",
        subtitle = "Shackleton Crater Rim — Lunar South Pole",
        gravity = "0.166 g (1.62 m/s²)",
        atmosphere = "Exosphere (< 10⁻¹² bar, vacuum)",
        surfaceTemp = "-246°C to +120°C",
        dayNightCycle = "29.5 Earth Days (Semi-permanent light at south pole peaks)",
        radiationHazard = "Severe: Galactic Cosmic Rays & Solar Particle Events, no magnetic field",
        commDelay = "~1.28 seconds (Near-instant telemetry)",
        waterAvailability = "Water ice concentrated in Permanently Shadowed Regions (PSRs)",
        baseSolarIrradiance = 1.0f,
        scienceFact = "NASA’s Lunar Reconnaissance Orbiter (LRO) confirmed billions of tons of water ice trapped inside permanently shadowed craters at the Moon’s south pole. This ice can be split into hydrogen fuel and breathable oxygen.",
        nasaSourceUrl = "https://science.nasa.gov/moon/"
    ),
    MARS(
        title = "Mars",
        subtitle = "Jezero Crater Outpost — Western Isidis Planitia",
        gravity = "0.38 g (3.72 m/s²)",
        atmosphere = "95% CO₂, 0.006 bar (~0.6% Earth pressure)",
        surfaceTemp = "-125°C to +20°C (Average -60°C)",
        dayNightCycle = "24 hr 39 min 35 sec (1 Martian Sol)",
        radiationHazard = "High: Cosmic rays penetrate thin atmosphere; unshielded surface receives ~250 mSv/yr",
        commDelay = "4 to 24 minutes (One-way latency depending on orbital distance)",
        waterAvailability = "Subsurface permafrost, polar caps, and hydrated minerals; requires drilling/baking",
        baseSolarIrradiance = 0.43f, // Mars is 1.52 AU from Sun, ~43% solar energy of Earth
        scienceFact = "NASA’s Perseverance rover demonstrated MOXIE (Mars Oxygen ISRU Experiment), successfully extracting breathable oxygen from the Martian carbon dioxide atmosphere at a rate of ~12 grams/hr.",
        nasaSourceUrl = "https://science.nasa.gov/mars/"
    )
}

enum class Difficulty(
    val displayName: String,
    val resourceMultiplier: Float,
    val eventFrequency: Float,
    val startingCredits: Int
) {
    EASY("Cadet (Easy)", 1.25f, 0.6f, 1600),
    STANDARD("Astronaut (Standard)", 1.0f, 1.0f, 1200),
    EXPERT("Commander (Expert)", 0.75f, 1.4f, 800)
}

data class MissionConfig(
    val destination: Destination = Destination.MOON,
    val crewSize: Int = 4,
    val difficulty: Difficulty = Difficulty.STANDARD,
    val targetDurationSols: Int = 30
)
