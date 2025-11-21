package com.productivitystreak.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.screens.add.AddEntryMenuSheet
import com.productivitystreak.ui.screens.add.HabitFormSheet
import com.productivitystreak.ui.screens.add.JournalFormSheet
import com.productivitystreak.ui.screens.add.VocabularyFormSheet
import com.productivitystreak.ui.screens.dashboard.DashboardScreen
import com.productivitystreak.ui.screens.discover.AssetDetailScreen
import com.productivitystreak.ui.screens.discover.DiscoverScreen
import com.productivitystreak.ui.screens.onboarding.OnboardingFlow
import com.productivitystreak.ui.screens.profile.ProfileScreen
import com.productivitystreak.ui.screens.stats.StatsScreen
import com.productivitystreak.ui.state.AddEntryType
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.UiMessageType
import com.productivitystreak.ui.theme.NeverZeroTheme
import kotlinx.coroutines.launch

enum class MainDestination { HOME, STATS, DISCOVER, PROFILE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeverZeroApp(
    uiState: AppUiState,
    onRefreshQuote: () -> Unit,
    onSelectStreak: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onAddOneOffTask: (String) -> Unit,
    onToggleOneOffTask: (String) -> Unit,
    onDeleteOneOffTask: (String) -> Unit,
    onSimulateTaskCompletion: (String, Int) -> Unit,
    onLogReadingProgress: (Int) -> Unit,
    onAddVocabularyWord: (String, String, String?) -> Unit,
    onToggleOnboardingCategory: (String) -> Unit,
    onSetOnboardingGoal: (String) -> Unit,
    onSetOnboardingCommitment: (Int, Int) -> Unit,
    onNextOnboardingStep: () -> Unit,
    onPreviousOnboardingStep: () -> Unit,
    onToggleOnboardingNotifications: (Boolean) -> Unit,
    onSetOnboardingReminderTime: (String) -> Unit,
    onCompleteOnboarding: () -> Unit,
    onDismissOnboarding: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onChangeReminderFrequency: (com.productivitystreak.ui.state.profile.ReminderFrequency) -> Unit,
    onToggleWeeklySummary: (Boolean) -> Unit,
    onChangeTheme: (com.productivitystreak.ui.state.profile.ProfileTheme) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onSettingsThemeChange: (com.productivitystreak.ui.state.settings.ThemeMode) -> Unit = {},
    onSettingsDailyRemindersToggle: (Boolean) -> Unit = {},
    onSettingsWeeklyBackupsToggle: (Boolean) -> Unit = {},
    onSettingsReminderTimeChange: (String) -> Unit = {},
    onSettingsHapticFeedbackToggle: (Boolean) -> Unit = {},
    onSettingsCreateBackup: () -> Unit = {},
    onSettingsRestoreBackup: () -> Unit = {},
    onSettingsRestoreFileSelected: (Uri) -> Unit = {},
    onSettingsDismissRestoreDialog: () -> Unit = {},
    onSettingsDismissMessage: () -> Unit = {},
    onAssetConsumed: (String) -> Unit = {},
    onAssetTestPassed: (String) -> Unit = {},
    onCreateTimeCapsule: (message: String, goal: String, daysFromNow: Int) -> Unit = { _, _, _ -> },
    onSaveTimeCapsuleReflection: (id: String, reflection: String) -> Unit = { _, _ -> },
    onDismissUiMessage: () -> Unit,
    onOpenAddEntry: () -> Unit,
    onAddButtonTapped: () -> Unit,
    onDismissAddMenu: () -> Unit,
    onAddEntrySelected: (AddEntryType) -> Unit,
    onDismissAddForm: () -> Unit,
    onSubmitHabit: (name: String, goal: Int, unit: String, category: String, color: String?, icon: String?) -> Unit,
    onSubmitWord: (word: String, definition: String, example: String?) -> Unit,
    onSubmitJournal: (mood: Int, notes: String, highlights: String?, gratitude: String?, tomorrowGoals: String?) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    // FTUE: Immersive onboarding flow
    if (uiState.showOnboarding == true) {
        OnboardingFlow(
            uiState = uiState,
            onToggleOnboardingCategory = onToggleOnboardingCategory,
            onSetOnboardingGoal = onSetOnboardingGoal,
            onSetOnboardingCommitment = onSetOnboardingCommitment,
            onNextStep = onNextOnboardingStep,
            onPreviousStep = onPreviousOnboardingStep,
            onToggleNotificationsAllowed = onToggleOnboardingNotifications,
            onSetReminderTime = onSetOnboardingReminderTime,
            onCompleteOnboarding = onCompleteOnboarding,
            onDismissOnboarding = onDismissOnboarding,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onRequestExactAlarmPermission = onRequestExactAlarmPermission
        )
        return
    } else if (uiState.showOnboarding == null) {
        // Loading state - show splash or empty surface
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(contentAlignment = Alignment.Center) {
                // Optional: Add Logo or Loading Indicator here
            }
        }
        return
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val uiMessage = uiState.uiMessage
    val scope = rememberCoroutineScope()
    var snackbarType by remember { mutableStateOf(UiMessageType.INFO) }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            snackbarType = message.type
            scope.launch {
                snackbarHostState.showSnackbar(message.text)
                onDismissUiMessage()
            }
        }
    }

    var currentDestination by rememberSaveable { mutableStateOf(MainDestination.HOME) }
    var selectedAssetId by rememberSaveable { mutableStateOf<String?>(null) }
    var showSkillPaths by rememberSaveable { mutableStateOf(false) }

    val addUi = uiState.addUiState
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isSheetVisible = addUi.isMenuOpen || addUi.activeForm != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                val (icon, iconTint) = when (snackbarType) {
                    UiMessageType.SUCCESS -> Icons.Filled.CheckCircle to MaterialTheme.colorScheme.primary
                    UiMessageType.ERROR -> Icons.Filled.Warning to MaterialTheme.colorScheme.error
                    UiMessageType.INFO -> Icons.Filled.Info to MaterialTheme.colorScheme.secondary
                }
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    tonalElevation = 6.dp,
                    shadowElevation = 10.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedContent(targetState = iconTint, label = "snackbar-icon") { tint ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = tint
                            )
                        }
                        Text(
                            text = snackbarData.visuals.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        bottomBar = {
            NeverZeroBottomBar(
                current = currentDestination,
                onDestinationSelected = { destination ->
                    if (destination == MainDestination.HOME && currentDestination == MainDestination.HOME) return@NeverZeroBottomBar
                    currentDestination = destination
                    if (uiState.profileState.hapticsEnabled) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                },
                onAddTapped = {
                    if (uiState.profileState.hapticsEnabled) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onAddButtonTapped()
                },
                isMenuOpen = isSheetVisible
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(
                    // Add blur to background when Command Center is open
                    if (isSheetVisible) Modifier.blur(10.dp) else Modifier
                )
        ) {
            Crossfade(
                targetState = currentDestination,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "main-nav"
            ) { destination ->
                when (destination) {
                    MainDestination.HOME -> DashboardScreen(
                        uiState = uiState,
                        onToggleTask = onToggleTask,
                        onRefreshQuote = onRefreshQuote,
                        onAddHabitClick = onAddButtonTapped,
                        onSelectStreak = onSelectStreak,
                        onAddOneOffTask = onAddOneOffTask,
                        onToggleOneOffTask = onToggleOneOffTask,
                        onDeleteOneOffTask = onDeleteOneOffTask
                    )
                    MainDestination.STATS -> StatsScreen(
                        statsState = uiState.statsState,
                        onNavigateToSkillPaths = { showSkillPaths = true }
                    )
                    MainDestination.DISCOVER -> DiscoverScreen(
                        state = uiState.discoverState,
                        onAssetSelected = { assetId -> selectedAssetId = assetId }
                    )
                    MainDestination.PROFILE -> ProfileScreen(
                        userName = uiState.userName,
                        profileState = uiState.profileState,
                        settingsState = uiState.settingsState,
                        totalPoints = uiState.totalPoints,
                        timeCapsules = uiState.timeCapsules,
                        onSettingsThemeChange = onSettingsThemeChange,
                        onSettingsDailyRemindersToggle = onSettingsDailyRemindersToggle,
                        onSettingsWeeklyBackupsToggle = onSettingsWeeklyBackupsToggle,
                        onSettingsReminderTimeChange = onSettingsReminderTimeChange,
                        onSettingsHapticFeedbackToggle = onSettingsHapticFeedbackToggle,
                        onSettingsCreateBackup = onSettingsCreateBackup,
                        onSettingsRestoreBackup = onSettingsRestoreBackup,
                        onSettingsRestoreFileSelected = onSettingsRestoreFileSelected,
                        onSettingsDismissRestoreDialog = onSettingsDismissRestoreDialog,
                        onSettingsDismissMessage = onSettingsDismissMessage,
                        onToggleNotifications = onToggleNotifications,
                        onChangeReminderFrequency = onChangeReminderFrequency,
                        onToggleWeeklySummary = onToggleWeeklySummary,
                        onToggleHaptics = onToggleHaptics,
                        onRequestNotificationPermission = onRequestNotificationPermission,
                        onRequestExactAlarmPermission = onRequestExactAlarmPermission,
                        onCreateTimeCapsule = onCreateTimeCapsule,
                        onSaveTimeCapsuleReflection = onSaveTimeCapsuleReflection
                    )
                }
            }

            // Skill Paths Overlay
            if (showSkillPaths) {
                com.productivitystreak.ui.screens.skills.SkillPathsScreen(
                    onBack = { showSkillPaths = false },
                    onPathSelected = { /* TODO: show skill-path detail */ },
                    paths = uiState.skillPathsState.pathsProgress
                )
            }

            if (isSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        if (addUi.activeForm != null) onDismissAddForm() else onDismissAddMenu()
                    },
                    sheetState = sheetState,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                ) {
                    when (addUi.activeForm) {
                        null -> AddEntryMenuSheet(onEntrySelected = onAddEntrySelected)
                        AddEntryType.HABIT -> HabitFormSheet(
                            isSubmitting = addUi.isSubmitting,
                            onSubmit = onSubmitHabit
                        )
                        AddEntryType.WORD -> VocabularyFormSheet(
                            isSubmitting = addUi.isSubmitting,
                            onSubmit = onSubmitWord
                        )
                        AddEntryType.JOURNAL -> JournalFormSheet(
                            isSubmitting = addUi.isSubmitting,
                            onSubmit = onSubmitJournal
                        )
                    }
                }
            }

            val selectedAsset = selectedAssetId?.let { id ->
                uiState.discoverState.assets.firstOrNull { it.id == id }
            }

            if (selectedAsset != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    AssetDetailScreen(
                        asset = selectedAsset,
                        onDismiss = { selectedAssetId = null },
                        onComplete = { onAssetConsumed(selectedAsset.id) },
                        onTestPassed = { onAssetTestPassed(selectedAsset.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NeverZeroBottomBar(
    current: MainDestination,
    onDestinationSelected: (MainDestination) -> Unit,
    onAddTapped: () -> Unit,
    isMenuOpen: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = com.productivitystreak.ui.theme.Spacing.md, vertical = com.productivitystreak.ui.theme.Spacing.sm),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .blur(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            tonalElevation = com.productivitystreak.ui.theme.Elevation.level2,
            shape = com.productivitystreak.ui.theme.Shapes.extraLarge
        ) {}

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            shape = com.productivitystreak.ui.theme.Shapes.extraLarge
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = com.productivitystreak.ui.theme.Spacing.lg, vertical = com.productivitystreak.ui.theme.Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavItem(
                    icon = Icons.Outlined.Home,
                    label = "Home",
                    selected = current == MainDestination.HOME,
                    onClick = { onDestinationSelected(MainDestination.HOME) }
                )
                NavItem(
                    icon = Icons.Outlined.BarChart,
                    label = "Stats",
                    selected = current == MainDestination.STATS,
                    onClick = { onDestinationSelected(MainDestination.STATS) }
                )

                com.productivitystreak.ui.components.StyledFAB(
                    icon = Icons.Outlined.Add,
                    onClick = onAddTapped,
                    containerColor = NeverZeroTheme.gradientColors.PremiumStart,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    isExpanded = isMenuOpen
                )

                NavItem(
                    icon = Icons.Outlined.Search,
                    label = "Discover",
                    selected = current == MainDestination.DISCOVER,
                    onClick = { onDestinationSelected(MainDestination.DISCOVER) }
                )
                NavItem(
                    icon = Icons.Outlined.Person,
                    label = "Profile",
                    selected = current == MainDestination.PROFILE,
                    onClick = { onDestinationSelected(MainDestination.PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        label = "scale"
    )
    
    val color by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun DiscoverPlaceholder() { /* no-op: replaced by DiscoverScreen */ }

@Composable
private fun ProfilePlaceholder(userName: String) { /* no-op: replaced by ProfileScreen */ }
