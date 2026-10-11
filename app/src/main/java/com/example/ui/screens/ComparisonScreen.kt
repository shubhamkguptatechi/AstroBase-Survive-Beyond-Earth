package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Destination
import com.example.ui.theme.*

@Composable
fun ComparisonScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val moon = Destination.MOON
    val mars = Destination.MARS

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("compare_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyanAccent
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "PLANETARY SCIENCE ANALYSIS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Moon vs. Mars Environment",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Introduction & Scientific Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SpaceCardBg)
                    .border(1.dp, SpaceCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Operating an outpost on the Moon differs fundamentally from Mars. Human mission planners cannot use identical engineering profiles: lunar outposts prioritize extreme temperature swings and vacuum seals, while Mars outposts focus on solar distance and atmospheric dust storms.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comparative Metrics
            ComparisonRow(
                attribute = "Surface Gravity",
                moonVal = moon.gravity,
                marsVal = mars.gravity,
                scienceInsight = "Moon has ~1/6th Earth gravity; Mars has ~3/8th. Astronauts on Mars experience greater biomechanical stress, but bone density loss occurs on both without exercise."
            )

            Spacer(modifier = Modifier.height(10.dp))

            ComparisonRow(
                attribute = "Atmosphere & Pressure",
                moonVal = moon.atmosphere,
                marsVal = mars.atmosphere,
                scienceInsight = "Moon is hard vacuum requiring heavy pressurization. Mars has thin CO₂ air allowing aerocapture braking, wind dynamics, and MOXIE oxygen extraction."
            )

            Spacer(modifier = Modifier.height(10.dp))

            ComparisonRow(
                attribute = "Surface Temperatures",
                moonVal = moon.surfaceTemp,
                marsVal = mars.surfaceTemp,
                scienceInsight = "Without an atmosphere, lunar surface swings 360°C between direct sunlight and shadow. Mars stays cold (average -60°C), but diurnal swings are milder."
            )

            Spacer(modifier = Modifier.height(10.dp))

            ComparisonRow(
                attribute = "Solar Energy Irradiance",
                moonVal = "1361 W/m² (1.0 AU from Sun)",
                marsVal = "590 W/m² (1.52 AU, ~43% Earth)",
                scienceInsight = "Solar arrays on Mars produce less than half the electrical power of identical lunar arrays. Mars dust storms further dim irradiance by up to 90%."
            )

            Spacer(modifier = Modifier.height(10.dp))

            ComparisonRow(
                attribute = "Radio Telemetry Latency",
                moonVal = moon.commDelay,
                marsVal = mars.commDelay,
                scienceInsight = "Lunar crews can converse with Earth in near real-time. Mars crews must resolve emergencies autonomously due to 8-48 minute round-trip light speed delays."
            )

            Spacer(modifier = Modifier.height(10.dp))

            ComparisonRow(
                attribute = "In-Situ Water Availability",
                moonVal = moon.waterAvailability,
                marsVal = mars.waterAvailability,
                scienceInsight = "Moon requires mining cryogenic crater floor ice at 30 K. Mars has subsurface sheets of glacier ice, polar cap water, and clay-bound hydration minerals."
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Official NASA Exploration Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(moon.nasaSourceUrl))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(CyanAccent)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "NASA Moon", tint = CyanAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("NASA Moon Portal", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mars.nasaSourceUrl))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MarsRust)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "NASA Mars", tint = MarsRust, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("NASA Mars Portal", color = MarsRust, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ComparisonRow(
    attribute: String,
    moonVal: String,
    marsVal: String,
    scienceInsight: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SpaceCardBg)
            .border(1.dp, SpaceCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Text(
            text = attribute.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = WarningAmber,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Moon Box
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceDark)
                    .padding(8.dp)
            ) {
                Text(
                    text = "MOON (LUNA)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = moonVal,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                )
            }

            // Mars Box
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceDark)
                    .padding(8.dp)
            ) {
                Text(
                    text = "MARS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MarsRust,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = marsVal,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "💡 $scienceInsight",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        )
    }
}
