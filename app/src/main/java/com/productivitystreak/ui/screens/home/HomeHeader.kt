package com.productivitystreak.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivitystreak.data.model.Quote
import com.productivitystreak.ui.theme.NeverZeroTheme
import com.productivitystreak.ui.theme.PlayfairFontFamily
import java.util.Calendar

/**
 * Home screen header with time-aware greeting and animated XP progress bar.
 */
@Composable
fun HomeHeader(
    userName: String,
    quote: Quote?,
    level: Int,
    currentXp: Int,
    xpToNextLevel: Int,
    modifier: Modifier = Modifier
) {
    val greeting = remember { getTimeBasedGreeting() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top row: Greeting + Level Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$greeting, $userName",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = PlayfairFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = NeverZeroTheme.designColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            LevelBadge(
                level = level,
                currentXp = currentXp,
                xpToNextLevel = xpToNextLevel
            )
        }

        // Animated XP Progress Bar (thin, below greeting)
        AnimatedXpProgressBar(
            currentXp = currentXp,
            xpToNextLevel = xpToNextLevel,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AnimatedXpProgressBar(
    currentXp: Int,
    xpToNextLevel: Int,
    modifier: Modifier = Modifier
) {
    val totalLevelXp = currentXp + xpToNextLevel
    val targetProgress = if (totalLevelXp > 0) currentXp.toFloat() / totalLevelXp else 0f
    
    // Animate progress changes smoothly
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "xp-progress"
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = NeverZeroTheme.designColors.primary,
            trackColor = NeverZeroTheme.designColors.primary.copy(alpha = 0.15f),
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$currentXp XP",
                style = MaterialTheme.typography.labelSmall,
                color = NeverZeroTheme.designColors.textSecondary
            )
            Text(
                text = "${currentXp + xpToNextLevel} XP to level up",
                style = MaterialTheme.typography.labelSmall,
                color = NeverZeroTheme.designColors.textSecondary
            )
        }
    }
}

@Composable
fun LevelBadge(
    level: Int,
    currentXp: Int,
    xpToNextLevel: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "LVL $level",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = NeverZeroTheme.designColors.primary
        )
    }
}

private fun getTimeBasedGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
