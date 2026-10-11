package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.EventChoice
import com.example.data.model.EventSeverity
import com.example.data.model.MissionEvent
import com.example.ui.theme.*

@Composable
fun EventDecisionDialog(
    event: MissionEvent,
    userCredits: Int,
    onChoiceSelected: (EventChoice) -> Unit
) {
    var selectedChoice by remember { mutableStateOf<EventChoice?>(null) }

    val severityColor = when (event.severity) {
        EventSeverity.CRITICAL -> DangerRed
        EventSeverity.WARNING -> WarningAmber
        EventSeverity.INFO -> CyanAccent
    }

    Dialog(
        onDismissRequest = { /* Cannot dismiss without making decision */ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("event_dialog")
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, severityColor.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
            color = SpaceAbyss
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (event.severity == EventSeverity.CRITICAL) Icons.Default.Warning else Icons.Default.Info,
                        contentDescription = "Alert",
                        tint = severityColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "OUTPOST TELEMETRY ALERT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = severityColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // NASA Scientific context card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SpaceCardBg)
                        .border(1.dp, CyanAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Science Context",
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NASA SCIENTIFIC PRINCIPLE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.scienceExplanation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SELECT PROTOCOL RESPONSE:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Options list
                event.choices.forEach { choice ->
                    val isSelected = selectedChoice?.id == choice.id
                    val canAfford = choice.costCredits <= userCredits

                    val borderColor = when {
                        isSelected -> CyanAccent
                        !canAfford -> SpaceCardBorder.copy(alpha = 0.3f)
                        else -> SpaceCardBorder
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SpaceCardBg.copy(alpha = 0.9f) else SpaceDark.copy(alpha = 0.8f))
                            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = canAfford) { selectedChoice = choice }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = choice.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (canAfford) TextPrimary else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (choice.costCredits > 0) {
                                Text(
                                    text = "${choice.costCredits} ¢",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (canAfford) WarningAmber else DangerRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            } else if (choice.costCredits < 0) {
                                Text(
                                    text = "+${-choice.costCredits} ¢ (Grant)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = choice.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Impacts badges
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (choice.deltaOxygen != 0f) {
                                StatChip("O₂", choice.deltaOxygen)
                            }
                            if (choice.deltaWater != 0f) {
                                StatChip("H₂O", choice.deltaWater)
                            }
                            if (choice.deltaFood != 0f) {
                                StatChip("Food", choice.deltaFood)
                            }
                            if (choice.deltaEnergy != 0f) {
                                StatChip("Energy", choice.deltaEnergy)
                            }
                            if (choice.deltaHealth != 0f) {
                                StatChip("Health", choice.deltaHealth)
                            }
                            if (choice.deltaIntegrity != 0f) {
                                StatChip("Integrity", choice.deltaIntegrity)
                            }
                            if (choice.deltaRadiationRisk != 0f) {
                                StatChip("Rad", -choice.deltaRadiationRisk) // inverse
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action button
                Button(
                    onClick = {
                        selectedChoice?.let { onChoiceSelected(it) }
                    },
                    enabled = selectedChoice != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_event_decision"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = SpaceDark
                    )
                ) {
                    Text(
                        text = "CONFIRM PROTOCOL EXECUTION",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, delta: Float) {
    val isPositive = delta > 0
    val chipColor = if (isPositive) SuccessGreen else DangerRed
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(chipColor.copy(alpha = 0.15f))
            .border(1.dp, chipColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$label ${if (isPositive) "+${delta.toInt()}" else "${delta.toInt()}"}%",
            color = chipColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
