package com.productivitystreak.ui.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.RpgStats
import com.productivitystreak.ui.components.RpgHexagon
import com.productivitystreak.ui.theme.PlayfairFontFamily

/**
 * Compact Radar Chart preview for Bento grid.
 * Shows user stats in a small hexagon format.
 */
@Composable
fun RadarChartPreview(
    stats: RpgStats,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deepForest = Color(0xFF1A2C24)
    val creamWhite = Color(0xFFF5F5DC)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(28.dp),
        color = deepForest,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Stats",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = PlayfairFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = creamWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                RpgHexagon(
                    stats = stats,
                    modifier = Modifier.size(120.dp),
                    size = 120.dp,
                    lineColor = creamWhite.copy(alpha = 0.25f),
                    strokeColor = creamWhite,
                    fillColor = creamWhite.copy(alpha = 0.2f)
                )
            }

            Text(
                text = "Level ${stats.level}",
                style = MaterialTheme.typography.labelMedium,
                color = creamWhite.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
