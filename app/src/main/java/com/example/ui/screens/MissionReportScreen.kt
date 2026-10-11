package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameState
import com.example.data.model.MissionStatus
import com.example.ui.theme.*

@Composable
fun MissionReportScreen(
    state: GameState,
    onPlayAgain: () -> Unit,
    onReturnHome: () -> Unit
) {
    val context = LocalContext.current
    val isVictory = state.status == MissionStatus.VICTORY
    val report = state.failureAnalysis

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onReturnHome,
                    modifier = Modifier.testTag("report_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Home",
                        tint = CyanAccent
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "DEBRIEF TELEMETRY REPORT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = if (isVictory) "MISSION SUCCESSFUL" else "MISSION CRITICAL FAILURE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = if (isVictory) SuccessGreen else DangerRed,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Status Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SpaceCardBg)
                    .border(
                        2.dp,
                        if (isVictory) SuccessGreen else DangerRed,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(if (isVictory) SuccessGreen.copy(alpha = 0.2f) else DangerRed.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isVictory) Icons.Default.EmojiEvents else Icons.Default.Dangerous,
                        contentDescription = "Status",
                        tint = if (isVictory) SuccessGreen else DangerRed,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isVictory) "OUTPOST GOAL ATTAINED" else "SURVIVAL PROTOCOL BREACHED",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${state.config.destination.title.uppercase()} • Sol ${state.currentSol} of ${state.config.targetDurationSols} Sols • Score: ${state.score} pts",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SPECIAL FEATURE: MISSION FAILURE ANALYSIS
            if (!isVictory && report != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DangerRed.copy(alpha = 0.1f))
                        .border(1.dp, DangerRed.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Analysis",
                            tint = DangerRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MISSION FAILURE ROOT-CAUSE ANALYSIS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = DangerRed,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Primary Cause: ${report.primaryCause}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Root Mechanism: ${report.rootMechanism}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Exhausted Resource: ${report.exhaustedResource}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WarningAmber,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Critical Decision Factor: ${report.criticalDecision}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // NASA Science Debrief Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SpaceDark)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "NASA SCIENTIFIC DEBRIEF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.scienceDebrief,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "TACTICAL ADVICE FOR NEXT COMMANDER:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    report.tacticalAdvice.forEachIndexed { i, advice ->
                        Text(
                            text = "• $advice",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Final Resource Readout Summary
            Text(
                text = "FINAL RESOURCE TELEMETRY",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpaceCardBg)
                    .border(1.dp, SpaceCardBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                ResourceSummaryRow("Oxygen (O₂)", "${state.oxygen.toInt()}%", state.oxygen > 15f)
                ResourceSummaryRow("Potable Water", "${state.water.toInt()}%", state.water > 15f)
                ResourceSummaryRow("Food Reserves", "${state.food.toInt()}%", state.food > 15f)
                ResourceSummaryRow("Battery Energy", "${state.energy.toInt()}% (${state.batteryStoredKWh.toInt()} kWh)", state.energy > 15f)
                ResourceSummaryRow("Crew Health", "${state.crewHealth.toInt()}%", state.crewHealth > 20f)
                ResourceSummaryRow("Habitat Integrity", "${state.habitatIntegrity.toInt()}%", state.habitatIntegrity > 25f)
                ResourceSummaryRow("Credits Remaining", "${state.credits} ¢", true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Timeline of Key Events
            Text(
                text = "MISSION TIMELINE & EVENT LOG",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpaceCardBg)
                    .border(1.dp, SpaceCardBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                if (state.eventHistory.isEmpty()) {
                    Text("No mission incidents recorded.", color = TextMuted, fontSize = 12.sp)
                } else {
                    state.eventHistory.takeLast(8).reversed().forEach { log ->
                        TimelineRow(log.sol, log.title, log.outcomeSummary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("play_again_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = SpaceDark
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = "Play Again")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PLAY AGAIN (NEW MISSION)", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    val shareSummary = """
                        🚀 AstroBase: Junior Astronaut Mission Trainer Report
                        Target: ${state.config.destination.title} (${state.config.destination.subtitle})
                        Status: ${if (isVictory) "MISSION SUCCESSFUL" else "MISSION FAILED (Sol ${state.currentSol})"}
                        Final Score: ${state.score} pts
                        Crew Complement: ${state.config.crewSize} astronauts
                        Difficulty: ${state.config.difficulty.displayName}
                        NASA Space Apps Challenge 2026
                    """.trimIndent()
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareSummary)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Mission Telemetry"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("share_summary_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(SpaceCardBorder)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = CyanAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Mission Summary", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onReturnHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Return to Mission Setup", color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ResourceSummaryRow(label: String, value: String, isOk: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isOk) TextPrimary else DangerRed,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun TimelineRow(sol: Int, title: String, outcome: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SpaceDark)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "SOL $sol",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
            Text(
                text = outcome,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )
        }
    }
}
