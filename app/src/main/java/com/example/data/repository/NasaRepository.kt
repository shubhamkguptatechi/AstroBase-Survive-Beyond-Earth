package com.example.data.repository

import com.example.data.model.ApodItem
import com.example.data.model.NasaImageItem
import com.example.data.model.ScienceTopic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class NasaRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    suspend fun searchNasaImages(query: String): List<NasaImageItem> = withContext(Dispatchers.IO) {
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        val url = "https://images-api.nasa.gov/search?q=$encoded&media_type=image"
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonStr = response.body?.string().orEmpty()
                val parsed = parseNasaSearchJson(jsonStr)
                if (parsed.isNotEmpty()) {
                    return@withContext parsed.take(12)
                }
            }
        } catch (e: Exception) {
            // Network fallback
        }
        // Return reliable curated items matching query or fallback
        getCuratedNasaImages(query)
    }

    suspend fun fetchApod(): ApodItem = withContext(Dispatchers.IO) {
        // Fallback curated APOD
        val fallback = ApodItem(
            date = "2026-10-11",
            title = "South Pole of the Moon: Peaks of Light and Ice",
            explanation = "At the lunar south pole, high crater ridges such as Malapert Mountain receive semi-permanent sunlight, while adjacent crater floors remain in perpetual shadow at -246°C, preserving ancient volatile water ice.",
            url = "https://images.nasa.gov/details/PIA13517",
            copyright = "NASA / GSFC / Arizona State University"
        )
        try {
            val request = Request.Builder()
                .url("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY")
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string().orEmpty())
                return@withContext ApodItem(
                    date = json.optString("date", "Today"),
                    title = json.optString("title", fallback.title),
                    explanation = json.optString("explanation", fallback.explanation),
                    url = json.optString("url", fallback.url),
                    copyright = json.optString("copyright", "NASA"),
                    mediaType = json.optString("media_type", "image")
                )
            }
        } catch (_: Exception) {}
        fallback
    }

    private fun parseNasaSearchJson(jsonStr: String): List<NasaImageItem> {
        val results = mutableListOf<NasaImageItem>()
        try {
            val root = JSONObject(jsonStr)
            val collection = root.getJSONObject("collection")
            val items = collection.getJSONArray("items")
            for (i in 0 until minOf(items.length(), 20)) {
                val item = items.getJSONObject(i)
                val dataArr = item.getJSONArray("data")
                if (dataArr.length() == 0) continue
                val data = dataArr.getJSONObject(0)
                val nasaId = data.optString("nasa_id", "NASA-$i")
                val title = data.optString("title", "NASA Asset")
                val description = data.optString("description", "")
                val center = data.optString("center", "NASA")
                val dateCreated = data.optString("date_created", "").take(10)

                var imageUrl = ""
                if (item.has("links")) {
                    val links = item.getJSONArray("links")
                    for (j in 0 until links.length()) {
                        val link = links.getJSONObject(j)
                        if (link.optString("rel") == "preview" || link.optString("render") == "image") {
                            imageUrl = link.optString("href")
                            break
                        }
                    }
                }

                if (imageUrl.isNotEmpty()) {
                    results.add(
                        NasaImageItem(
                            nasaId = nasaId,
                            title = title,
                            description = description,
                            imageUrl = imageUrl,
                            center = center,
                            dateCreated = dateCreated,
                            sourceWebUrl = "https://images.nasa.gov/details/$nasaId"
                        )
                    )
                }
            }
        } catch (_: Exception) {}
        return results
    }

    fun getCuratedNasaImages(category: String = ""): List<NasaImageItem> {
        return listOf(
            NasaImageItem(
                nasaId = "PIA13517",
                title = "Moon Shackleton Crater Rim",
                description = "Lunar South Pole mosaic acquired by NASA's Lunar Reconnaissance Orbiter Camera (LROC). Shows illuminated rims and deep dark interior.",
                imageUrl = "https://images-assets.nasa.gov/image/PIA13517/PIA13517~thumb.jpg",
                center = "GSFC",
                dateCreated = "2023",
                sourceWebUrl = "https://images.nasa.gov/details/PIA13517"
            ),
            NasaImageItem(
                nasaId = "PIA24434",
                title = "Perseverance MOXIE Experiment on Mars",
                description = "NASA's Mars Oxygen In-Situ Resource Utilization Experiment (MOXIE) demonstrated extracting breathable oxygen from the Martian CO₂ atmosphere.",
                imageUrl = "https://images-assets.nasa.gov/image/PIA24434/PIA24434~thumb.jpg",
                center = "JPL",
                dateCreated = "2021",
                sourceWebUrl = "https://images.nasa.gov/details/PIA24434"
            ),
            NasaImageItem(
                nasaId = "iss064e029272",
                title = "ISS ECLSS Advanced Water Processor",
                description = "Astronaut inspects water recycling filtration hardware aboard the International Space Station, achieving 98% closed-loop recovery.",
                imageUrl = "https://images-assets.nasa.gov/image/iss064e029272/iss064e029272~thumb.jpg",
                center = "JSC",
                dateCreated = "2021",
                sourceWebUrl = "https://images.nasa.gov/details/iss064e029272"
            ),
            NasaImageItem(
                nasaId = "artemis_base_camp",
                title = "Artemis Base Camp Concept Architecture",
                description = "Artist rendering of NASA's Artemis Base Camp at the lunar south pole featuring modern habitats, pressurized rovers, and solar arrays.",
                imageUrl = "https://images-assets.nasa.gov/image/NHQ202004020001/NHQ202004020001~thumb.jpg",
                center = "HQ",
                dateCreated = "2020",
                sourceWebUrl = "https://images.nasa.gov/details/NHQ202004020001"
            ),
            NasaImageItem(
                nasaId = "PIA23319",
                title = "Curiosity Rover Gale Crater Panorama",
                description = "NASA's Curiosity rover examines layered sediment and atmospheric haze across the Martian surface.",
                imageUrl = "https://images-assets.nasa.gov/image/PIA23319/PIA23319~thumb.jpg",
                center = "JPL",
                dateCreated = "2019",
                sourceWebUrl = "https://images.nasa.gov/details/PIA23319"
            ),
            NasaImageItem(
                nasaId = "iss054e054124",
                title = "Veggie Plant Growth Chamber on ISS",
                description = "Fresh red romaine lettuce harvested in space under magenta LED light recipes for astronaut nutrition research.",
                imageUrl = "https://images-assets.nasa.gov/image/iss054e054124/iss054e054124~thumb.jpg",
                center = "KSC",
                dateCreated = "2018",
                sourceWebUrl = "https://images.nasa.gov/details/iss054e054124"
            )
        )
    }

    fun getScienceTopics(): List<ScienceTopic> {
        return listOf(
            ScienceTopic(
                id = "TOPIC_ISRU_WATER",
                category = "ISRU & Resources",
                title = "Lunar Ice & In-Situ Resource Extraction",
                summary = "Permanently shadowed craters at the lunar south pole trap billions of kilograms of cryogenic water ice.",
                detailedText = "NASA’s Lunar Crater Observation and Sensing Satellite (LCROSS) and Lunar Reconnaissance Orbiter (LRO) confirmed that deep craters like Shackleton and Faustini have temperatures plunging below 30 Kelvin (-243°C). At these temperatures, water molecules deposited by comets cannot evaporate and remain locked in regolith. Processing this ice through thermal sublimation produces drinkable water, cooling fluids, and liquid oxygen/liquid hydrogen for rocket propellant.",
                nasaMission = "LRO / LCROSS / VIPER",
                sourceUrl = "https://science.nasa.gov/moon/",
                keyTakeaways = listOf(
                    "South pole crater interiors never receive direct sunlight.",
                    "1 liter of water launched from Earth costs thousands of dollars; ISRU makes long-term outposts economically viable.",
                    "Electrolyzing lunar water creates clean oxygen for breathing and rocket fuel."
                ),
                simulationConnection = "In AstroBase, In-Situ extraction events allow commanders to deploy thermal core drills to restock water and oxygen without costly Earth resupply."
            ),
            ScienceTopic(
                id = "TOPIC_MOXIE_O2",
                category = "Life Support (ECLSS)",
                title = "MOXIE: Extracting Oxygen from Martian Atmosphere",
                summary = "The Perseverance rover demonstrated that solid oxide electrolysis can harvest breathable oxygen directly from Mars' thin CO₂ air.",
                detailedText = "Mars has an atmospheric pressure of ~610 Pascals consisting of 95% carbon dioxide. NASA’s MOXIE experiment sucks in ambient air, compresses and filters dust, and heats it to 800°C across solid-oxide electrolysis ceramic tubes. The electrochemical reaction splits CO₂ molecules into oxygen ions and carbon monoxide exhaust (2 CO₂ → 2 CO + O₂). MOXIE proved a scaled system could support human crews indefinitely.",
                nasaMission = "Mars 2020 / Perseverance Rover",
                sourceUrl = "https://science.nasa.gov/mars/",
                keyTakeaways = listOf(
                    "MOXIE generated 122 grams of oxygen over 16 operations on Mars.",
                    "Solid oxide electrolysis operates reliably across cold Martian nights and dust storms.",
                    "A scaled unit of 25-30 kW would produce enough O₂ to launch a Mars ascent rocket."
                ),
                simulationConnection = "In AstroBase, purchasing the 'Sabatier & MOXIE Oxygen Recycler' upgrade reduces crew O₂ consumption by 45%, requiring a predictable 3 kW power draw."
            ),
            ScienceTopic(
                id = "TOPIC_RADIATION",
                category = "Crew Health & Safety",
                title = "Space Radiation & Regolith Shielding",
                summary = "Without Earth's thick atmosphere and magnetosphere, spacefarers face galactic cosmic rays and solar proton events.",
                detailedText = "Extraterrestrial radiation consists of two major hazards: Galactic Cosmic Rays (GCRs) — highly energetic atomic nuclei stripped of electrons traveling near light speed from outside our solar system — and Solar Particle Events (SPEs) — bursts of high-energy protons ejected during coronal mass ejections. SPEs are acutely lethal without prompt shelter. Research shows that burying habitats beneath 1 to 2 meters of local regolith attenuates GCR dosage to near-terrestrial background levels.",
                nasaMission = "Artemis I / Orion RAD & Matroshka Phantoms",
                sourceUrl = "https://www.nasa.gov/humans-in-space/artemis/",
                keyTakeaways = listOf(
                    "Galactic Cosmic Rays penetrate standard aluminum hull shielding easily.",
                    "Hydrogen-rich materials (water bladders, polyethylene) and dense regolith are optimal shields.",
                    "Coronal mass ejections give hours of advance warning from space observatories to evacuate crews to storm shelters."
                ),
                simulationConnection = "In AstroBase, commanders must balance radiation exposure risk. Constructing 3D-sintered regolith shielding slashes radiation risk by 40% and preserves crew health."
            ),
            ScienceTopic(
                id = "TOPIC_WATER_ECLSS",
                category = "Life Support (ECLSS)",
                title = "Closed-Loop ECLSS Water Recovery",
                summary = "NASA's Environmental Control and Life Support System recycles 98% of all human moisture on the space station.",
                detailedText = "A human crew member needs ~2.5 to 3.0 liters of water daily for hydration and food rehydration. On the International Space Station, the ECLSS Water Processor Assembly (WPA) and Urine Processor Assembly (UPA) capture crew sweat, expired breath condensation, and urine. A vacuum distillation centrifuge separates water from urea and salts, followed by catalytic oxidation reactors and iodine disinfection, producing water cleaner than municipal tap water on Earth.",
                nasaMission = "International Space Station ECLSS",
                sourceUrl = "https://www.nasa.gov/reference/moonbase-about/",
                keyTakeaways = listOf(
                    "Recovers 98% of crew water, minimizing launch mass penalties.",
                    "Uses vacuum rotary distillation to operate without buoyant gravity.",
                    "Requires strict anti-crystallization chemistry to prevent pump failure."
                ),
                simulationConnection = "In AstroBase, the 'Vapor Compression Water Recovery ECLSS' upgrade reduces water drain by 55%, protecting the base against water pump cavitation emergencies."
            ),
            ScienceTopic(
                id = "TOPIC_SOLAR_POWER",
                category = "Energy & Storage",
                title = "Solar Irradiance: Moon vs. Mars",
                summary = "Solar power generation follows the inverse-square law: Mars receives less than half the solar irradiance of the Moon.",
                detailedText = "At Earth and the Moon (1.0 Astronomical Unit from the Sun), solar irradiance is ~1361 W/m². At Mars (average 1.52 AU), irradiance drops to ~590 W/m² (roughly 43%). Furthermore, Martian atmospheric dust can reduce surface irradiance by over 90% during global dust storms. On the Moon, while solar irradiance is high, the lunar night lasts 14 Earth days in equatorial regions, necessitating south pole crater rim placement where sunlight is semi-continuous.",
                nasaMission = "InSight / Opportunity / Artemis Power",
                sourceUrl = "https://science.nasa.gov/planetary-science/",
                keyTakeaways = listOf(
                    "Mars requires more than double the solar panel area of the Moon for the same electrical output.",
                    "Martian atmospheric dust storms can completely starve solar-powered landers.",
                    "Large battery storage buffers and regenerative fuel cells are essential."
                ),
                simulationConnection = "AstroBase accurately models lower baseline solar wattage on Mars (26 kW vs 32 kW on the Moon) and introduces dust storm attenuation events."
            ),
            ScienceTopic(
                id = "TOPIC_SPACE_FARMING",
                category = "Food & Agriculture",
                title = "Space Agriculture: Aeroponics & Veggie",
                summary = "Growing crops in space provides fresh micro-nutrients, psychological wellbeing, and atmospheric scrubbers.",
                detailedText = "NASA’s Veggie facility and Advanced Plant Habitat (APH) aboard the ISS test microgreens, dwarf peppers, and radishes in closed chambers. Because water does not flow naturally in microgravity or low gravity, roots are supported by porous ceramic clay or aeroponic nutrient misting. Photosynthesizing plants also consume carbon dioxide exhaled by astronauts and transpire purified humidity into the cabin atmosphere.",
                nasaMission = "Advanced Plant Habitat (APH) / Veggie",
                sourceUrl = "https://spaceplace.nasa.gov/",
                keyTakeaways = listOf(
                    "Fresh plants supply vitamins C and K which degrade in shelf-stable packaged rations.",
                    "Aeroponic misting uses 95% less water than terrestrial soil farming.",
                    "Plant care significantly improves astronaut mood and psychological resilience."
                ),
                simulationConnection = "In AstroBase, Aeroponic Growth Chambers boost food production by +7%/Sol and regenerate crew mental health, but require water and electrical power to operate."
            )
        )
    }
}
