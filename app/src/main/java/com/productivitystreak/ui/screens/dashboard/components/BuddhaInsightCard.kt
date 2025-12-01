package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.components.GlassCard
import com.productivitystreak.ui.components.PremiumGlassCard
import com.productivitystreak.ui.theme.NeverZeroTheme
import androidx.compose.material.icons.filled.Refresh

@Composable
fun BuddhaInsightCard(
    insight: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumGlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onRefresh() } // Tap anywhere to refresh/interact
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Refresh, // Or a better "Wisdom" icon if available
                        contentDescription = null,
                        tint = NeverZeroTheme.designColors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DAILY WISDOM",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeverZeroTheme.designColors.primary,
                        letterSpacing = 2.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
                
                // The Wisdom Content
                Text(
                    text = insight,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = 32.sp
                    ),
                    color = NeverZeroTheme.designColors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Footer / CTA
                Text(
                    text = "Tap to reflect",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeverZeroTheme.designColors.textSecondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}
