package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAdvisorService
import com.example.data.model.*
import com.example.data.repository.NasaRepository
import com.example.data.scenario.MissionScenarios
import com.example.game.GameEngine
import com.example.ui.navigation.AstroScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AstroUiState(
    val currentScreen: AstroScreen = AstroScreen.WELCOME,
    val pendingConfig: MissionConfig = MissionConfig(),
    val gameState: GameState = GameEngine.initializeMission(MissionConfig()),
    val upgrades: List<UpgradeItem> = MissionScenarios.getDefaultUpgrades(),
    val isAdvisorOpen: Boolean = false,
    val isAdvisorLoading: Boolean = false,
    val advisorText: String = "",
    val advisorQuestion: String = "",
    val nasaImages: List<NasaImageItem> = emptyList(),
    val isNasaLoading: Boolean = false,
    val nasaSearchQuery: String = "Moon habitat",
    val apodItem: ApodItem? = null,
    val scienceTopics: List<ScienceTopic> = emptyList(),
    val selectedScienceTopic: ScienceTopic? = null,
    val toastMessage: String? = null
)

class AstroBaseViewModel(
    private val nasaRepository: NasaRepository = NasaRepository(),
    private val advisorService: GeminiAdvisorService = GeminiAdvisorService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AstroUiState())
    val uiState: StateFlow<AstroUiState> = _uiState.asStateFlow()

    init {
        loadScienceData()
    }

    private fun loadScienceData() {
        val topics = nasaRepository.getScienceTopics()
        val initialCurated = nasaRepository.getCuratedNasaImages()
        _uiState.update {
            it.copy(
                scienceTopics = topics,
                nasaImages = initialCurated
            )
        }

        viewModelScope.launch {
            val apod = nasaRepository.fetchApod()
            _uiState.update { it.copy(apodItem = apod) }
        }
    }

    fun navigateTo(screen: AstroScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun updatePendingConfig(config: MissionConfig) {
        _uiState.update { it.copy(pendingConfig = config) }
    }

    fun startMissionSetup(config: MissionConfig) {
        _uiState.update {
            it.copy(
                pendingConfig = config,
                currentScreen = AstroScreen.BRIEFING
            )
        }
    }

    fun launchMission() {
        val config = _uiState.value.pendingConfig
        val freshState = GameEngine.initializeMission(config)
        val freshUpgrades = MissionScenarios.getDefaultUpgrades()
        _uiState.update {
            it.copy(
                gameState = freshState,
                upgrades = freshUpgrades,
                currentScreen = AstroScreen.DASHBOARD,
                toastMessage = "Touchdown confirmed at ${config.destination.title}!"
            )
        }
    }

    fun advanceSol() {
        val current = _uiState.value.gameState
        if (current.isGameOver) {
            _uiState.update { it.copy(currentScreen = AstroScreen.MISSION_REPORT) }
            return
        }
        if (current.activeEmergency != null) {
            _uiState.update { it.copy(toastMessage = "Resolve the active emergency before advancing Sol!") }
            return
        }

        val nextState = GameEngine.advanceSol(current)
        _uiState.update {
            val nextScreen = if (nextState.isGameOver) AstroScreen.MISSION_REPORT else it.currentScreen
            it.copy(
                gameState = nextState,
                currentScreen = nextScreen,
                toastMessage = if (nextState.status == MissionStatus.VICTORY) "Mission Accomplished!"
                else if (nextState.status == MissionStatus.FAILED) "Mission Failure Detected! Reviewing telemetry..."
                else "Advanced to Sol ${nextState.currentSol}"
            )
        }
    }

    fun purchaseUpgrade(upgrade: UpgradeItem) {
        val current = _uiState.value.gameState
        val (updatedState, success) = GameEngine.applyUpgrade(current, upgrade)
        if (success) {
            // Update upgrade count in UI list
            val updatedUpgrades = _uiState.value.upgrades.map { item ->
                if (item.id == upgrade.id) {
                    item.copy(installedCount = (item.installedCount + 1))
                } else item
            }
            _uiState.update {
                it.copy(
                    gameState = updatedState,
                    upgrades = updatedUpgrades,
                    toastMessage = "Installed: ${upgrade.name}"
                )
            }
        } else {
            _uiState.update {
                it.copy(toastMessage = if (current.credits < upgrade.costCredits) "Insufficient construction credits!" else "Upgrade maxed out!")
            }
        }
    }

    fun resolveEmergency(event: MissionEvent, choice: EventChoice) {
        val current = _uiState.value.gameState
        val updatedState = GameEngine.resolveEvent(current, event, choice)
        _uiState.update {
            val nextScreen = if (updatedState.isGameOver) AstroScreen.MISSION_REPORT else it.currentScreen
            it.copy(
                gameState = updatedState,
                currentScreen = nextScreen,
                toastMessage = "Response protocol executed."
            )
        }
    }

    fun openAdvisor(question: String? = null) {
        _uiState.update {
            it.copy(
                isAdvisorOpen = true,
                isAdvisorLoading = true,
                advisorQuestion = question.orEmpty()
            )
        }
        viewModelScope.launch {
            val advice = advisorService.getMissionAdvice(_uiState.value.gameState, question)
            _uiState.update {
                it.copy(
                    isAdvisorLoading = false,
                    advisorText = advice
                )
            }
        }
    }

    fun closeAdvisor() {
        _uiState.update { it.copy(isAdvisorOpen = false) }
    }

    fun searchNasaImages(query: String) {
        _uiState.update { it.copy(isNasaLoading = true, nasaSearchQuery = query) }
        viewModelScope.launch {
            val images = nasaRepository.searchNasaImages(query)
            _uiState.update {
                it.copy(
                    isNasaLoading = false,
                    nasaImages = images
                )
            }
        }
    }

    fun selectScienceTopic(topic: ScienceTopic?) {
        _uiState.update { it.copy(selectedScienceTopic = topic) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun playAgain() {
        val currentConfig = _uiState.value.gameState.config
        val freshState = GameEngine.initializeMission(currentConfig)
        val freshUpgrades = MissionScenarios.getDefaultUpgrades()
        _uiState.update {
            it.copy(
                gameState = freshState,
                upgrades = freshUpgrades,
                currentScreen = AstroScreen.DASHBOARD,
                toastMessage = "New mission initiated!"
            )
        }
    }

    fun returnToHome() {
        _uiState.update { it.copy(currentScreen = AstroScreen.WELCOME) }
    }
}
