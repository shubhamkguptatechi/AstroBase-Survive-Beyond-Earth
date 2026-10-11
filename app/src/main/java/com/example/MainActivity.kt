package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CapcomAdvisorSheet
import com.example.ui.components.EventDecisionDialog
import com.example.ui.components.ScienceDetailDialog
import com.example.ui.navigation.AstroScreen
import com.example.ui.screens.*
import com.example.ui.theme.AstroBaseTheme
import com.example.ui.theme.SpaceDark
import com.example.ui.viewmodel.AstroBaseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstroBaseTheme {
                AstroBaseApp()
            }
        }
    }
}

@Composable
fun AstroBaseApp(
    viewModel: AstroBaseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Show toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Back handling for custom navigation
    BackHandler(enabled = uiState.currentScreen != AstroScreen.WELCOME) {
        when (uiState.currentScreen) {
            AstroScreen.BRIEFING -> viewModel.navigateTo(AstroScreen.WELCOME)
            AstroScreen.UPGRADES -> viewModel.navigateTo(AstroScreen.DASHBOARD)
            AstroScreen.COMPARISON -> viewModel.navigateTo(if (uiState.gameState.currentSol > 1) AstroScreen.DASHBOARD else AstroScreen.WELCOME)
            AstroScreen.SCIENCE_EXPLORER -> viewModel.navigateTo(if (uiState.gameState.currentSol > 1) AstroScreen.DASHBOARD else AstroScreen.WELCOME)
            AstroScreen.CHALLENGE_ALIGNMENT -> viewModel.navigateTo(AstroScreen.WELCOME)
            AstroScreen.MISSION_REPORT -> viewModel.navigateTo(AstroScreen.WELCOME)
            AstroScreen.DASHBOARD -> viewModel.navigateTo(AstroScreen.WELCOME)
            AstroScreen.WELCOME -> {}
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceDark),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SpaceDark)
        ) {
            when (uiState.currentScreen) {
                AstroScreen.WELCOME -> {
                    WelcomeScreen(
                        currentConfig = uiState.pendingConfig,
                        onConfigChanged = { viewModel.updatePendingConfig(it) },
                        onStartMission = { viewModel.startMissionSetup(it) },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
                AstroScreen.BRIEFING -> {
                    BriefingScreen(
                        config = uiState.pendingConfig,
                        onBack = { viewModel.navigateTo(AstroScreen.WELCOME) },
                        onLaunch = { viewModel.launchMission() }
                    )
                }
                AstroScreen.DASHBOARD -> {
                    DashboardScreen(
                        state = uiState.gameState,
                        onAdvanceSol = { viewModel.advanceSol() },
                        onOpenUpgrades = { viewModel.navigateTo(AstroScreen.UPGRADES) },
                        onOpenAdvisor = { viewModel.openAdvisor() },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
                AstroScreen.UPGRADES -> {
                    UpgradesScreen(
                        state = uiState.gameState,
                        upgrades = uiState.upgrades,
                        onBack = { viewModel.navigateTo(AstroScreen.DASHBOARD) },
                        onPurchaseUpgrade = { viewModel.purchaseUpgrade(it) }
                    )
                }
                AstroScreen.COMPARISON -> {
                    ComparisonScreen(
                        onBack = {
                            viewModel.navigateTo(if (uiState.gameState.currentSol > 1) AstroScreen.DASHBOARD else AstroScreen.WELCOME)
                        }
                    )
                }
                AstroScreen.SCIENCE_EXPLORER -> {
                    ScienceExplorerScreen(
                        nasaImages = uiState.nasaImages,
                        isNasaLoading = uiState.isNasaLoading,
                        currentQuery = uiState.nasaSearchQuery,
                        apodItem = uiState.apodItem,
                        scienceTopics = uiState.scienceTopics,
                        onSearch = { viewModel.searchNasaImages(it) },
                        onSelectTopic = { viewModel.selectScienceTopic(it) },
                        onBack = {
                            viewModel.navigateTo(if (uiState.gameState.currentSol > 1) AstroScreen.DASHBOARD else AstroScreen.WELCOME)
                        }
                    )
                }
                AstroScreen.MISSION_REPORT -> {
                    MissionReportScreen(
                        state = uiState.gameState,
                        onPlayAgain = { viewModel.playAgain() },
                        onReturnHome = { viewModel.returnToHome() }
                    )
                }
                AstroScreen.CHALLENGE_ALIGNMENT -> {
                    ChallengeAlignmentScreen(
                        onBack = { viewModel.navigateTo(AstroScreen.WELCOME) }
                    )
                }
            }

            // Active Emergency Decision Modal
            uiState.gameState.activeEmergency?.let { emergency ->
                EventDecisionDialog(
                    event = emergency,
                    userCredits = uiState.gameState.credits,
                    onChoiceSelected = { choice ->
                        viewModel.resolveEmergency(emergency, choice)
                    }
                )
            }

            // CAPCOM AI Mission Advisor Bottom Sheet / Modal
            CapcomAdvisorSheet(
                isOpen = uiState.isAdvisorOpen,
                isLoading = uiState.isAdvisorLoading,
                advisorText = uiState.advisorText,
                onDismiss = { viewModel.closeAdvisor() },
                onAskQuestion = { viewModel.openAdvisor(it) }
            )

            // Science Topic In-Depth Dialog
            ScienceDetailDialog(
                topic = uiState.selectedScienceTopic,
                onDismiss = { viewModel.selectScienceTopic(null) }
            )
        }
    }
}
