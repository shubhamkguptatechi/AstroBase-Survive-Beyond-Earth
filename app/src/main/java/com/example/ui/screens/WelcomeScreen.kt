package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Destination
import com.example.data.model.Difficulty
import com.example.data.model.MissionConfig
import com.example.ui.navigation.AstroScreen
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
    currentConfig: MissionConfig,
    onConfigChanged: (MissionConfig) -> Unit,
    onStartMission: (MissionConfig) -> Unit,
    onNavigate: (AstroScreen) -> Unit
) {
    var selectedDestination by remember { mutableStateOf(currentConfig.destination) }
    var selectedCrew by remember { mutableIntStateOf(currentConfig.crewSize) }
    var selectedDifficulty by remember { mutableStateOf(currentConfig.difficulty) }
    var selectedDuration by remember { mutableIntStateOf(currentConfig.targetDurationSols) }

    fun emitConfig() {
        val cfg = MissionConfig(
            destination = selectedDestination,
            crewSize = selectedCrew,
            difficulty = selectedDifficulty,
            targetDurationSols = selectedDuration
        )
        onConfigChanged(cfg)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // App Title & Badge Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(CyanAccent.copy(alpha = 0.2f))
                        .border(1.dp, CyanAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = "AstroBase Emblem",
                        tint = CyanAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ASTROBASE",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp,
                            fontSize = 26.sp
                        )
                    )
                    Text(
                        text = "SURVIVE BEYOND EARTH",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Destination Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, SpaceCardBorder, RoundedCornerShape(16.dp))
            ) {
                val heroRes = if (selectedDestination == Destination.MOON) R.drawable.lunar_base_hero else R.drawable.mars_base_hero
                Image(
                    painter = painterResource(id = heroRes),
                    contentDescription = "Outpost Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, SpaceDark.copy(alpha = 0.9f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "TARGET DESTINATION: ${selectedDestination.title.uppercase()}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = selectedDestination.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Challenge Description Tagline
            Text(
                text = "“Your decisions. Their survival. Humanity's next frontier.”",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = WarningAmber,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Command an extraterrestrial research habitat. Balance life-support ECLSS, solar power, food growth, and cosmic radiation under real NASA spaceflight constraints.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // STEP 1: Destination Selection
            Text(
                text = "1. CHOOSE DESTINATION",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DestinationCard(
                    title = "Moon (Luna)",
                    desc = "0.166 g • Vacuum • South Pole Ice",
                    isSelected = selectedDestination == Destination.MOON,
                    modifier = Modifier.weight(1f),
                    testTag = "dest_moon_button"
                ) {
                    selectedDestination = Destination.MOON
                    emitConfig()
                }
                DestinationCard(
                    title = "Mars",
                    desc = "0.38 g • 95% CO₂ • Dust Storms",
                    isSelected = selectedDestination == Destination.MARS,
                    modifier = Modifier.weight(1f),
                    testTag = "dest_mars_button"
                ) {
                    selectedDestination = Destination.MARS
                    emitConfig()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 2: Crew Size
            Text(
                text = "2. CREW COMPLEMENT",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(2, 4, 6).forEach { size ->
                    val isSel = selectedCrew == size
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) SpaceCardBg else SpaceDark)
                            .border(1.dp, if (isSel) CyanAccent else SpaceCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                selectedCrew = size
                                emitConfig()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$size Astronauts",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isSel) CyanAccent else TextSecondary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 3: Difficulty
            Text(
                text = "3. MISSION DIFFICULTY",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Difficulty.values().forEach { diff ->
                    val isSel = selectedDifficulty == diff
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) SpaceCardBg else SpaceDark)
                            .border(1.dp, if (isSel) WarningAmber else SpaceCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                selectedDifficulty = diff
                                emitConfig()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = diff.displayName.split(" ")[0],
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isSel) WarningAmber else TextSecondary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 4: Duration
            Text(
                text = "4. TARGET MISSION DURATION",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 30, 60).forEach { sols ->
                    val isSel = selectedDuration == sols
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) SpaceCardBg else SpaceDark)
                            .border(1.dp, if (isSel) NeonTeal else SpaceCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                selectedDuration = sols
                                emitConfig()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$sols Sols",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isSel) NeonTeal else TextSecondary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Scientific Model Disclaimer Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SpaceCardBg.copy(alpha = 0.6f))
                    .border(1.dp, SpaceCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Educational Disclaimer",
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Science Model Note: AstroBase is an educational Junior Astronaut Trainer using simplified life-support mathematics grounded in NASA open data, not an aerospace flight certification tool.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Launch Button
            Button(
                onClick = {
                    val cfg = MissionConfig(
                        destination = selectedDestination,
                        crewSize = selectedCrew,
                        difficulty = selectedDifficulty,
                        targetDurationSols = selectedDuration
                    )
                    onStartMission(cfg)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_mission_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = SpaceDark
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START MISSION BRIEFING",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Exploration Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(AstroScreen.SCIENCE_EXPLORER) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("explore_science_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(CyanAccent, ElectricBlue)))
                ) {
                    Text(
                        text = "NASA Science",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { onNavigate(AstroScreen.COMPARISON) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("compare_planets_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(ElectricBlue, MarsRust)))
                ) {
                    Text(
                        text = "Moon vs Mars",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { onNavigate(AstroScreen.CHALLENGE_ALIGNMENT) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("challenge_alignment_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningAmber),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(WarningAmber, MarsRust)))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Challenge",
                        tint = WarningAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NASA Space Apps 2026 Challenge Alignment",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DestinationCard(
    title: String,
    desc: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) SpaceCardBg else SpaceDark)
            .border(
                2.dp,
                if (isSelected) CyanAccent else SpaceCardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (isSelected) CyanAccent else TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 11.sp
            )
        )
    }
}
