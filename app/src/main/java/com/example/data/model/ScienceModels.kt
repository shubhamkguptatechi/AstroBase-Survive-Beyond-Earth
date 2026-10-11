package com.example.data.model

data class NasaImageItem(
    val nasaId: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val center: String = "NASA",
    val dateCreated: String = "2024",
    val sourceWebUrl: String = "https://images.nasa.gov"
)

data class ApodItem(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val copyright: String? = null,
    val mediaType: String = "image"
)

data class ScienceTopic(
    val id: String,
    val category: String,
    val title: String,
    val summary: String,
    val detailedText: String,
    val nasaMission: String,
    val sourceUrl: String,
    val keyTakeaways: List<String>,
    val simulationConnection: String // How AstroBase models this concept!
)
