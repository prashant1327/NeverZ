package com.productivitystreak.ui.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.productivitystreak.ui.state.AddEntryType
import com.productivitystreak.ui.state.AppUiState

@Suppress("UNUSED_PARAMETER")
@Composable
fun NeverZeroApp(
    uiState: AppUiState,
    onRefreshQuote: () -> Unit,
    onSelectStreak: (String) -> Unit,
    onToggleTask: (String) -> Unit,
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
    onOpenAddEntry: () -> Unit,
    onAddButtonTapped: () -> Unit,
    onDismissAddMenu: () -> Unit,
    onAddEntrySelected: (AddEntryType) -> Unit,
    onDismissAddForm: () -> Unit,
    onSubmitHabit: (name: String, goal: Int, unit: String, category: String, color: String?, icon: String?) -> Unit,
    onSubmitWord: (word: String, definition: String, example: String?) -> Unit,
    onSubmitJournal: (mood: Int, notes: String, highlights: String?, gratitude: String?, tomorrowGoals: String?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "UI Cleanup Complete",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
