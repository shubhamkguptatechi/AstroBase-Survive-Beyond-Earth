package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.Destination
import com.example.data.model.GameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAdvisorService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun getMissionAdvice(state: GameState, userQuestion: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && apiKey.length > 10) {
            try {
                val prompt = buildGeminiPrompt(state, userQuestion)
                val responseText = callGeminiApi(apiKey, prompt)
                if (responseText.isNotBlank()) {
                    return@withContext responseText
                }
            } catch (_: Exception) {
                // Fallback to rule-based analysis
            }
        }

        generateRuleBasedAdvice(state, userQuestion)
    }

    private fun callGeminiApi(apiKey: String, prompt: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "You are CAPCOM-AI, an expert NASA Flight Director and Junior Astronaut Mission Advisor for human outposts on the Moon and Mars. Provide concise, scientifically accurate, actionable tactical advice to the student commander based on their live telemetry. Keep responses structured, professional, and encouraging (under 250 words).")
                    })
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)
        val request = Request.Builder().url(url).post(requestBody).build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val respJson = JSONObject(response.body?.string().orEmpty())
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return parts.getJSONObject(0).optString("text")
                }
            }
        }
        return ""
    }

    private fun buildGeminiPrompt(state: GameState, userQuestion: String?): String {
        return """
            MISSION TELEMETRY READOUT:
            - Destination: ${state.config.destination.title} (${state.config.destination.subtitle})
            - Sol: ${state.currentSol} of ${state.config.targetDurationSols}
            - Crew Size: ${state.config.crewSize} astronauts
            - Difficulty: ${state.config.difficulty.displayName}
            - Credits Available: ${state.credits}
            
            RESOURCE GAUGES:
            - Oxygen: ${state.oxygen.toInt()}%
            - Potable Water: ${state.water.toInt()}%
            - Food Reserves: ${state.food.toInt()}%
            - Battery Energy: ${state.energy.toInt()}% (${state.batteryStoredKWh.toInt()} / ${state.maxBatteryKWh.toInt()} kWh)
            - Net Power Balance: Solar ${state.solarGenerationKW} kW vs Demand ${state.powerDemandKW} kW (Net: ${state.netPowerBalanceKW} kW)
            - Crew Health: ${state.crewHealth.toInt()}%
            - Ambient Radiation Exposure: ${state.radiationRisk.toInt()}%
            - Habitat Structural Integrity: ${state.habitatIntegrity.toInt()}%
            - Upgrades Installed: ${state.installedUpgrades.keys.joinToString(", ").ifEmpty { "None" }}
            
            ${if (userQuestion.isNullOrBlank()) "TASK: Diagnose the current outpost status, identify the single greatest survival risk, and recommend 2 concrete upgrades or actions for the next Sol." else "STUDENT COMMANDER QUERY: $userQuestion"}
        """.trimIndent()
    }

    fun generateRuleBasedAdvice(state: GameState, userQuestion: String?): String {
        val destination = state.config.destination
        val risks = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        // Analyze power
        if (state.isPowerDeficit) {
            risks.add("⚡ **Power Deficit**: Outpost consumption (${state.powerDemandKW} kW) exceeds solar generation (${state.solarGenerationKW} kW). Battery storage is actively depleting.")
            recommendations.add("Deploy a **High-Efficiency Photovoltaic Wing** (+14 kW) to maintain positive daily battery charging.")
        } else if (state.energy < 25f) {
            risks.add("🔋 **Low Battery Storage**: Stored energy is at ${state.energy.toInt()}%. A single shadow or dust storm could trigger an ECLSS blackout.")
            recommendations.add("Purchase a **Lithium-Titanate Energy Storage Rack** to expand reserve capacity.")
        }

        // Analyze O2
        if (state.oxygen < 30f) {
            risks.add("🫁 **Critical Oxygen Depletion**: O₂ levels are down to ${state.oxygen.toInt()}%. Risk of cabin hypoxia and asphyxiation within a few Sols.")
            recommendations.add("Install the **Sabatier & MOXIE Oxygen Recycler** to cut daily breathing gas drain by 45%.")
        }

        // Analyze Water
        if (state.water < 30f) {
            risks.add("💧 **Water Shortage**: Potable water is at ${state.water.toInt()}%. Dehydration directly damages crew health and prevents crop cultivation.")
            recommendations.add("Install the **Vapor Compression Water Recovery ECLSS** to recycle 90% of moisture.")
        }

        // Analyze Integrity
        if (state.habitatIntegrity < 40f) {
            risks.add("🛡️ **Hull Structural Fatigue**: Habitat integrity has deteriorated to ${state.habitatIntegrity.toInt()}%. Below 25%, microscopic leaks will vent cabin air into the vacuum.")
            recommendations.add("Acquire **Critical Maintenance Spares** or deploy **Sintered Regolith Shielding** immediately.")
        }

        // Analyze Radiation
        if (state.radiationRisk > 25f) {
            risks.add("☢️ **High Radiation Exposure**: Unshielded exposure is ${state.radiationRisk.toInt()}%. Solar and cosmic radiation will steadily degrade crew immune response.")
            recommendations.add("Construct 3D-printed **Sintered Regolith Radiation Shields** over the habitat shell.")
        }

        // Destination specific tips
        val destTip = when (destination) {
            Destination.MOON -> "At the lunar south pole, solar arrays need elevation atop crater ridges to catch low-angled sunlight; keep battery reserves primed for shadow transits."
            Destination.MARS -> "On Mars, atmospheric distance reduces solar strength to ~43% of Earth. Guard against atmospheric dust storms that can persist for weeks."
        }

        val primaryRisk = if (risks.isNotEmpty()) risks.first() else "✅ **Telemetry Nominal**: All primary life-support systems, power grids, and habitat bulkheads are operating within green safety margins."
        val secondaryRec = if (recommendations.isNotEmpty()) recommendations.first() else "Continue regular scientific tasks and accumulate credits for future module expansions."

        return if (!userQuestion.isNullOrBlank()) {
            """
                🛰️ **CAPCOM Advisory Response:**
                
                Regarding your query: *"$userQuestion"*
                
                **Tactical Assessment:**
                $primaryRisk
                
                **NASA Scientific Guidance:**
                $destTip
                
                **Action Plan:**
                $secondaryRec
            """.trimIndent()
        } else {
            """
                🛰️ **CAPCOM Flight Director Telemetry Brief (Sol ${state.currentSol}):**
                
                **Primary Hazard Assessment:**
                $primaryRisk
                
                **Strategic Recommendation:**
                $secondaryRec
                
                **Environmental Context:**
                $destTip
            """.trimIndent()
        }
    }
}
