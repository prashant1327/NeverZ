package com.productivitystreak.ui.screens.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HapticsSettingsCard(
    enabled: Boolean,
    onSettingsHapticFeedbackToggle: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit
) {
    com.productivitystreak.ui.components.GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
