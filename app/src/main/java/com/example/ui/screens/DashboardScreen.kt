package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameState
import com.example.ui.components.HabitatVisualCard
import com.example.ui.components.ResourceCard
import com.example.ui.navigation.AstroScreen
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    state: GameState,
    onAdvanceSol: () -> Unit,
    onOpenUpgrades: () -> Unit,
    onOpenAdvisor: () -> Unit,
    onNavigate: (AstroScreen) -> Unit
) {
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
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MISSION CONTROL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${state.config.destination.title.uppercase()} COMMAND",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Score Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SpaceCardBg)
                        .border(1.dp, SpaceCardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Score",
                            tint = WarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.score} PTS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Outpost Banner
            HabitatVisualCard(state = state)

            Spacer(modifier = Modifier.height(14.dp))

            // Emergency Notice if active
            if (state.activeEmergency != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DangerRed.copy(alpha = 0.2f))
                        .border(1.dp, DangerRed, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency",
                            tint = DangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACTIVE ALERT: ${state.activeEmergency.title} — Decision Required!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Power Grid Telemetry Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpaceCardBg)
                    .border(1.dp, SpaceCardBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ELECTRICAL POWER BALANCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Generation: ${state.solarGenerationKW.toInt()} kW | Demand: ${state.powerDemandKW.toInt()} kW",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    val isNetPos = state.netPowerBalanceKW >= 0
                    val netCol = if (isNetPos) SuccessGreen else DangerRed
                    Text(
                        text = if (isNetPos) "+${state.netPowerBalanceKW.toInt()} kW Net" else "${state.netPowerBalanceKW.toInt()} kW Net",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = netCol,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 8 Telemetry Gauges Grid (2 columns)
            Text(
                text = "LIFE SUPPORT & SYSTEMS TELEMETRY",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Row 1: Oxygen & Water
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceCard(
                    title = "Oxygen (O₂)",
                    value = state.oxygen,
                    icon = Icons.Default.Air,
                    normalColor = CyanAccent,
                    testTag = "gauge_oxygen",
                    subtitle = "Partial Pressure 21 kPa",
                    modifier = Modifier.weight(1f)
                )
                ResourceCard(
                    title = "Potable Water",
                    value = state.water,
                    icon = Icons.Default.WaterDrop,
                    normalColor = ElectricBlue,
                    testTag = "gauge_water",
                    subtitle = "Recycled ECLSS Reservoir",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Food & Energy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceCard(
                    title = "Food Reserves",
                    value = state.food,
                    icon = Icons.Default.Restaurant,
                    normalColor = NeonTeal,
                    testTag = "gauge_food",
                    subtitle = "Dehydrated & Crops",
                    modifier = Modifier.weight(1f)
                )
                ResourceCard(
                    title = "Battery Energy",
                    value = state.energy,
                    icon = Icons.Default.BatteryChargingFull,
                    normalColor = WarningAmber,
                    testTag = "gauge_energy",
                    subtitle = "${state.batteryStoredKWh.toInt()} / ${state.maxBatteryKWh.toInt()} kWh",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 3: Health & Radiation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceCard(
                    title = "Crew Health",
                    value = state.crewHealth,
                    icon = Icons.Default.Favorite,
                    normalColor = SuccessGreen,
                    testTag = "gauge_health",
                    subtitle = "${state.config.crewSize} Vitals Monitored",
                    modifier = Modifier.weight(1f)
                )
                ResourceCard(
                    title = "Radiation Risk",
                    value = state.radiationRisk,
                    icon = Icons.Default.Warning,
                    normalColor = NeonTeal,
                    warningThreshold = 40f,
                    criticalThreshold = 20f,
                    isInverse = true,
                    testTag = "gauge_radiation",
                    subtitle = "Galactic Cosmic Rays",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 4: Habitat Integrity & Food Production Capacity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceCard(
                    title = "Hab Integrity",
                    value = state.habitatIntegrity,
                    icon = Icons.Default.Shield,
                    normalColor = CyanAccent,
                    testTag = "gauge_integrity",
                    subtitle = "Micrometeorite Bumper",
                    modifier = Modifier.weight(1f)
                )
                ResourceCard(
                    title = "Crop Capacity",
                    value = state.foodProductionCapacity,
                    unit = "%/Sol",
                    icon = Icons.Default.LocalFlorist,
                    normalColor = SuccessGreen,
                    testTag = "gauge_crops",
                    subtitle = "Aeroponic Growth Yield",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Mission Action Row: Next Sol + Engineering Upgrades
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Next Sol Button
                Button(
                    onClick = onAdvanceSol,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("next_sol_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = SpaceDark
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next Sol")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ADVANCE SOL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                // Engineering / Upgrades Button
                Button(
                    onClick = onOpenUpgrades,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("upgrades_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpaceCardBg,
                        contentColor = TextPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SpaceCardBorder)
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Construction,
                            contentDescription = "Upgrades",
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ENGINEER",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CAPCOM AI ADVISOR Banner Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpaceCardBg)
                    .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable { onOpenAdvisor() }
                    .testTag("open_advisor_button")
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "CAPCOM AI",
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CAPCOM MISSION ADVISOR (AI)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Get tactical risk diagnosis & flight advice",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Advisor",
                        tint = CyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(AstroScreen.SCIENCE_EXPLORER) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SpaceCardBorder)
                    )
                ) {
                    Text("NASA Science", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { onNavigate(AstroScreen.COMPARISON) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SpaceCardBorder)
                    )
                ) {
                    Text("Moon vs Mars", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { onNavigate(AstroScreen.MISSION_REPORT) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SpaceCardBorder)
                    )
                ) {
                    Text("Mission Log", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
