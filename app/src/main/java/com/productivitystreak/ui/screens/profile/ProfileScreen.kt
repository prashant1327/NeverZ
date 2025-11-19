package com.productivitystreak.ui.screens.profile

// Profile UI removed during architectural sanitization.

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.state.profile.ProfileState
import com.productivitystreak.ui.state.profile.ProfileTheme
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.state.settings.SettingsState
import com.productivitystreak.ui.state.settings.ThemeMode
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.utils.PermissionManager

@Composable
fun ProfileScreen(
    userName: String,
    profileState: ProfileState,
    settingsState: SettingsState,
    onSettingsThemeChange: (ThemeMode) -> Unit,
    onSettingsDailyRemindersToggle: (Boolean) -> Unit,
    onSettingsWeeklyBackupsToggle: (Boolean) -> Unit,
    onSettingsReminderTimeChange: (String) -> Unit,
    onSettingsHapticFeedbackToggle: (Boolean) -> Unit,
    onSettingsCreateBackup: () -> Unit,
    onSettingsRestoreBackup: () -> Unit,
    onSettingsRestoreFileSelected: (Uri) -> Unit,
    onSettingsDismissRestoreDialog: () -> Unit,
    onSettingsDismissMessage: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onChangeReminderFrequency: (ReminderFrequency) -> Unit,
    onToggleWeeklySummary: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Profile & Settings",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        AccountCard(userName = userName, email = profileState.email)

        NotificationPreferencesCard(
            profileState = profileState,
            settingsState = settingsState,
            onToggleNotifications = onToggleNotifications,
            onSettingsDailyRemindersToggle = onSettingsDailyRemindersToggle,
            onChangeReminderFrequency = onChangeReminderFrequency,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onRequestExactAlarmPermission = onRequestExactAlarmPermission
        )

        ThemeCard(
            settingsState = settingsState,
            onSettingsThemeChange = onSettingsThemeChange
        )

        HapticsCard(
            enabled = settingsState.hapticFeedbackEnabled,
            onSettingsHapticFeedbackToggle = onSettingsHapticFeedbackToggle,
            onToggleHaptics = onToggleHaptics
        )

        BackupCard(
            settingsState = settingsState,
            onCreateBackup = onSettingsCreateBackup,
            onRestoreBackup = onSettingsRestoreBackup,
            onSettingsRestoreFileSelected = onSettingsRestoreFileSelected
        )

        LegalLinks(profileState)
    }

    if (settingsState.errorMessage != null || settingsState.showBackupSuccessMessage || settingsState.showRestoreSuccessMessage || settingsState.showTimePickerDialog) {
        // Let the central snackbar represent messages; here we just clear flags when invoked externally
        // via onSettingsDismissMessage from NeverZeroApp when needed.
        // No additional dialogs to avoid duplication.
    }
}

@Composable
private fun AccountCard(userName: String, email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "N",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NotificationPreferencesCard(
    profileState: ProfileState,
    settingsState: SettingsState,
    onToggleNotifications: (Boolean) -> Unit,
    onSettingsDailyRemindersToggle: (Boolean) -> Unit,
    onChangeReminderFrequency: (ReminderFrequency) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit
) {
    val context = LocalContext.current
    val shouldRequestNotification = PermissionManager.shouldRequestNotificationPermission(context)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Reminders",
                style = MaterialTheme.typography.titleMedium
            )

            PreferenceRow(
                title = "Enable notifications",
                subtitle = "Allow Never Zero to send habit nudges.",
                checked = profileState.notificationEnabled,
                onCheckedChange = {
                    if (it) {
                        onRequestNotificationPermission()
                    }
                    onToggleNotifications(it)
                }
            )

            PreferenceRow(
                title = "Daily reminders",
                subtitle = "Receive a summary at your preferred time.",
                checked = settingsState.dailyRemindersEnabled,
                onCheckedChange = onSettingsDailyRemindersToggle
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reminder cadence",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = when (profileState.reminderFrequency) {
                            ReminderFrequency.Daily -> "Every day"
                            ReminderFrequency.Weekly -> "Weekly"
                            ReminderFrequency.None -> "Off"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FrequencyChip(
                        label = "Off",
                        selected = profileState.reminderFrequency == ReminderFrequency.None,
                        onClick = { onChangeReminderFrequency(ReminderFrequency.None) }
                    )
                    FrequencyChip(
                        label = "Weekly",
                        selected = profileState.reminderFrequency == ReminderFrequency.Weekly,
                        onClick = { onChangeReminderFrequency(ReminderFrequency.Weekly) }
                    )
                    FrequencyChip(
                        label = "Daily",
                        selected = profileState.reminderFrequency == ReminderFrequency.Daily,
                        onClick = { onChangeReminderFrequency(ReminderFrequency.Daily) }
                    )
                }
            }

            if (shouldRequestNotification) {
                Spacer(modifier = Modifier.height(8.dp))
                PermissionNudgeCard(onRequestNotificationPermission, onRequestExactAlarmPermission)
            }
        }
    }
}

@Composable
private fun PreferenceRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun FrequencyChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else Color.Transparent
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PermissionNudgeCard(
    onRequestNotificationPermission: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Enable Notifications for better tracking",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Turn notifications and alarms back on from system settings.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Open app settings",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
private fun ThemeCard(
    settingsState: SettingsState,
    onSettingsThemeChange: (ThemeMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Appearance", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeChip(
                    label = "Light",
                    selected = settingsState.themeMode == ThemeMode.LIGHT,
                    onClick = { onSettingsThemeChange(ThemeMode.LIGHT) }
                )
                ThemeChip(
                    label = "Dark",
                    selected = settingsState.themeMode == ThemeMode.DARK,
                    onClick = { onSettingsThemeChange(ThemeMode.DARK) }
                )
                ThemeChip(
                    label = "System",
                    selected = settingsState.themeMode == ThemeMode.SYSTEM,
                    onClick = { onSettingsThemeChange(ThemeMode.SYSTEM) }
                )
            }
        }
    }
}

@Composable
private fun ThemeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HapticsCard(
    enabled: Boolean,
    onSettingsHapticFeedbackToggle: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Haptics", style = MaterialTheme.typography.titleMedium)
            PreferenceRow(
                title = "Haptic feedback",
                subtitle = "Subtle vibrations on key actions.",
                checked = enabled,
                onCheckedChange = {
                    onSettingsHapticFeedbackToggle(it)
                    onToggleHaptics(it)
                }
            )
        }
    }
}

@Composable
private fun BackupCard(
    settingsState: SettingsState,
    onCreateBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onSettingsRestoreFileSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val restoreLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                onSettingsRestoreFileSelected(uri)
            }
        }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Backups", style = MaterialTheme.typography.titleMedium)
            Text(
                text = settingsState.lastBackupTime?.let { "Last backup: $it" } ?: "No backups yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Create backup",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(enabled = !settingsState.isBackupInProgress) { onCreateBackup() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
                Text(
                    text = "Restore from file",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(enabled = !settingsState.isRestoreInProgress) {
                            restoreLauncher.launch(arrayOf("application/json", "*/*"))
                            onRestoreBackup()
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun LegalLinks(profileState: ProfileState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Legal",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        profileState.legalLinks.forEach { item ->
            val context = LocalContext.current
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                    context.startActivity(intent)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

