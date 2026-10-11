package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ApodItem
import com.example.data.model.NasaImageItem
import com.example.data.model.ScienceTopic
import com.example.ui.theme.*

@Composable
fun ScienceExplorerScreen(
    nasaImages: List<NasaImageItem>,
    isNasaLoading: Boolean,
    currentQuery: String,
    apodItem: ApodItem?,
    scienceTopics: List<ScienceTopic>,
    onSearch: (String) -> Unit,
    onSelectTopic: (ScienceTopic) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf(currentQuery) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Topics, 1 = NASA Media, 2 = APOD

    val quickSearches = listOf("Moon habitat", "Mars rover", "Artemis", "ISS ECLSS", "Space agriculture")

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("science_back_button")
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
                        text = "NASA OPEN DATA & RESEARCH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "NASA Science Explorer",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton("Core Topics", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                TabButton("NASA Media API", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
                TabButton("APOD Astronomy", selectedTab == 2, Modifier.weight(1f)) { selectedTab = 2 }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> {
                    // Educational Topics List
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(scienceTopics) { topic ->
                            ScienceTopicCard(topic = topic, onClick = { onSelectTopic(topic) })
                        }
                    }
                }
                1 -> {
                    // NASA Image & Video Library API Search & Gallery
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search Input
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search NASA image archive...", color = TextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { onSearch(searchText) }) {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = CyanAccent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("nasa_search_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = SpaceCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Search Chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(quickSearches) { q ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SpaceCardBg)
                                        .border(1.dp, SpaceCardBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            searchText = q
                                            onSearch(q)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(text = q, color = CyanAccent, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isNasaLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = CyanAccent)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(nasaImages) { img ->
                                    NasaImageCard(item = img)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // APOD Display
                    if (apodItem != null) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SpaceCardBg)
                                        .border(1.dp, SpaceCardBorder, RoundedCornerShape(16.dp))
                                        .padding(14.dp)
                                ) {
                                    Text(
                                        text = "ASTRONOMY PICTURE OF THE DAY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = WarningAmber,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = apodItem.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Date: ${apodItem.date} • Credit: ${apodItem.copyright ?: "NASA"}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SpaceDark)
                                    ) {
                                        AsyncImage(
                                            model = apodItem.url,
                                            contentDescription = apodItem.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = apodItem.explanation,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    TextButton(
                                        onClick = {
                                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(apodItem.url))
                                            context.startActivity(browserIntent)
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "View", tint = CyanAccent, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("View High-Res NASA Asset", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(title: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CyanAccent.copy(alpha = 0.2f) else SpaceCardBg)
            .border(1.dp, if (isSelected) CyanAccent else SpaceCardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) CyanAccent else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun ScienceTopicCard(topic: ScienceTopic, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SpaceCardBg)
            .border(1.dp, SpaceCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = topic.category.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
            Text(
                text = topic.nasaMission,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = topic.title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = topic.summary,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tap to view in-depth research & simulation link →",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyanAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun NasaImageCard(item: NasaImageItem) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SpaceCardBg)
            .border(1.dp, SpaceCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SpaceDark)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.description.take(160) + if (item.description.length > 160) "..." else "",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NASA Center: ${item.center} • ${item.dateCreated}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                )
            )

            TextButton(
                onClick = {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceWebUrl))
                    context.startActivity(browserIntent)
                },
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Open Source", tint = CyanAccent, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("View NASA Asset", color = CyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
