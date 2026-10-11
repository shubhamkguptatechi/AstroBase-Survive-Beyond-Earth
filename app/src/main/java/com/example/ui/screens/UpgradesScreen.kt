package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.GameState
import com.example.data.model.UpgradeCategory
import com.example.data.model.UpgradeItem
import com.example.ui.theme.*

@Composable
fun UpgradesScreen(
    state: GameState,
    upgrades: List<UpgradeItem>,
    onBack: () -> Unit,
    onPurchaseUpgrade: (UpgradeItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<UpgradeCategory?>(null) }
    var pendingUpgradeToConfirm by remember { mutableStateOf<UpgradeItem?>(null) }

    val filteredUpgrades = if (selectedCategory == null) {
        upgrades
    } else {
        upgrades.filter { it.category == selectedCategory }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("upgrades_back_button")
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
                            text = "OUTPOST ENGINEERING",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Hardware Modules & ECLSS Upgrades",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Balance Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SpaceCardBg)
                        .border(1.dp, WarningAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Credits",
                            tint = WarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.credits} ¢",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = WarningAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isAll = selectedCategory == null
                    CategoryFilterChip("All Modules", isAll) { selectedCategory = null }
                }
                items(UpgradeCategory.values()) { cat ->
                    val isSel = selectedCategory == cat
                    CategoryFilterChip(cat.displayName, isSel) { selectedCategory = cat }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Upgrades List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredUpgrades) { item ->
                    val installedCount = state.installedUpgrades[item.id] ?: 0
                    val isMaxed = installedCount >= item.maxPurchases
                    val canAfford = state.credits >= item.costCredits && !isMaxed

                    UpgradeCard(
                        item = item,
                        installedCount = installedCount,
                        canAfford = canAfford,
                        isMaxed = isMaxed,
                        onInitiateInstall = { pendingUpgradeToConfirm = item }
                    )
                }
            }
        }

        // Confirmation Dialog
        if (pendingUpgradeToConfirm != null) {
            val item = pendingUpgradeToConfirm!!
            AlertDialog(
                onDismissRequest = { pendingUpgradeToConfirm = null },
                containerColor = SpaceAbyss,
                title = {
                    Text(
                        text = "Confirm Installation: ${item.name}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Cost: ${item.costCredits} credits (Balance after: ${state.credits - item.costCredits} ¢)",
                            color = WarningAmber,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Immediate Benefit: ${item.benefitText}",
                            color = SuccessGreen,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Operational Trade-off: ${item.tradeOffText}",
                            color = DangerRed,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "NASA Engineering Note: ${item.scienceNote}",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onPurchaseUpgrade(item)
                            pendingUpgradeToConfirm = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = SpaceDark
                        ),
                        modifier = Modifier.testTag("confirm_install_button")
                    ) {
                        Text("CONFIRM INSTALL", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingUpgradeToConfirm = null }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun CategoryFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CyanAccent.copy(alpha = 0.2f) else SpaceCardBg)
            .border(1.dp, if (isSelected) CyanAccent else SpaceCardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) CyanAccent else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun UpgradeCard(
    item: UpgradeItem,
    installedCount: Int,
    canAfford: Boolean,
    isMaxed: Boolean,
    onInitiateInstall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SpaceCardBg)
            .border(1.dp, SpaceCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = item.category.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent,
                        fontSize = 11.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SpaceDark)
                    .border(1.dp, SpaceCardBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Tier $installedCount/${item.maxPurchases}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isMaxed) SuccessGreen else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.description,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Benefit
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Benefit",
                tint = SuccessGreen,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.benefitText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SuccessGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
        }

        // Trade-off
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Trade-off",
                tint = DangerRed,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.tradeOffText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DangerRed.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Install Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cost: ${item.costCredits} ¢",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold
                )
            )

            Button(
                onClick = onInitiateInstall,
                enabled = canAfford,
                modifier = Modifier
                    .height(38.dp)
                    .testTag("install_${item.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = SpaceDark,
                    disabledContainerColor = SpaceDark,
                    disabledContentColor = TextMuted
                )
            ) {
                Text(
                    text = when {
                        isMaxed -> "MAX TIER"
                        !canAfford -> "CANNOT AFFORD"
                        else -> "INSTALL"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
