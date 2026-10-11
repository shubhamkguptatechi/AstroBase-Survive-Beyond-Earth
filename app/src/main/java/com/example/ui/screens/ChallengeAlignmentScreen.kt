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
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.example.ui.theme.*

@Composable
fun ChallengeAlignmentScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val challengeUrl = "https://www.spaceappschallenge.org/2026/challenges/build-a-junior-astronaut-mission-trainer/"

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
                    modifier = Modifier.testTag("challenge_back_button")
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
                        text = "NASA SPACE APPS CHALLENGE 2026",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WarningAmber,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Challenge Alignment Brief",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Challenge Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpaceCardBg)
                    .border(1.dp, WarningAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Challenge",
                        tint = WarningAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "“Build a Junior Astronaut Mission Trainer”",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AstroBase directly fulfills the NASA Space Apps Challenge 2026 requirements by transforming complex planetary life support and orbital constraints into an engaging, playable simulation for learners.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(challengeUrl))
                        context.startActivity(browserIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpaceDark),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(WarningAmber)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Open", tint = WarningAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Official NASA Challenge Page", color = WarningAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Alignment Checkpoints
            Text(
                text = "HOW ASTROBASE ADDRESSES REQUIREMENTS",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            AlignmentCard(
                title = "1. Playable Outpost Simulation Engine",
                desc = "Real simulation engine with deterministic resource decay (O₂, H₂O, food, energy), solar generation balancing, battery storage capacity, and Sol-by-Sol command progression."
            )

            Spacer(modifier = Modifier.height(8.dp))

            AlignmentCard(
                title = "2. Distinct Moon and Mars Environmental Models",
                desc = "Accurately distinguishes solar irradiance (1361 W/m² vs 590 W/m²), gravity (0.166g vs 0.38g), atmosphere (vacuum vs 95% CO₂), surface temperatures, and local hazards like Martian dust storms and lunar shadow transits."
            )

            Spacer(modifier = Modifier.height(8.dp))

            AlignmentCard(
                title = "3. Mission Failure Analysis Debrief",
                desc = "Special feature diagnosing root causes upon mission loss: identifies the depleted resource, physiological mechanism (hypoxia, dehydration, radiation), and provides 3 tactical NASA engineering recommendations."
            )

            Spacer(modifier = Modifier.height(8.dp))

            AlignmentCard(
                title = "4. Usable NASA Open Data & API Services",
                desc = "Integrates the NASA Image & Video Library API (images-api.nasa.gov) for genuine mission photography, Astronomy Picture of the Day (APOD), and verified science documentation on MOXIE, ECLSS, and Artemis."
            )

            Spacer(modifier = Modifier.height(8.dp))

            AlignmentCard(
                title = "5. CAPCOM AI Mission Advisor",
                desc = "Flight Director AI evaluating live outpost telemetry via Gemini AI (or built-in rule-based advisor fallback) to answer junior astronaut questions and explain hardware trade-offs."
            )

            Spacer(modifier = Modifier.height(8.dp))

            AlignmentCard(
                title = "6. Educational Transparency",
                desc = "Distinguishes simplified educational game numbers from certified engineering models, providing a Science Assumptions panel with verified NASA citations."
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AlignmentCard(title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SpaceCardBg)
            .border(1.dp, SpaceCardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = CyanAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        )
    }
}
