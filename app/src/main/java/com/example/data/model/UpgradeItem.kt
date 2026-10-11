package com.example.data.model

enum class UpgradeCategory(val displayName: String) {
    POWER("Energy & Storage"),
    LIFE_SUPPORT("Life Support (ECLSS)"),
    HABITAT("Structure & Radiation"),
    FOOD_BIO("Food & Agriculture"),
    LOGISTICS("Maintenance & Logistics")
}

data class UpgradeItem(
    val id: String,
    val name: String,
    val category: UpgradeCategory,
    val costCredits: Int,
    val description: String,
    val benefitText: String,
    val tradeOffText: String,
    val scienceNote: String,
    val iconName: String,
    // Statistical impacts on engine
    val solarBoostKW: Float = 0f,
    val batteryCapacityBoostKWh: Float = 0f,
    val oxygenRecyclingRateBoost: Float = 0f, // reduces net O2 drain
    val waterRecoveryRateBoost: Float = 0f, // reduces net water drain
    val foodGrowthRateBoost: Float = 0f, // % food produced per Sol
    val powerDemandKW: Float = 0f, // extra power draw
    val radiationShieldingBoost: Float = 0f, // reduces exposure %
    val habitatIntegrityBoost: Float = 0f, // immediate repair / durability
    val maxPurchases: Int = 3,
    val installedCount: Int = 0
) {
    val isMaxed: Boolean get() = installedCount >= maxPurchases
}
