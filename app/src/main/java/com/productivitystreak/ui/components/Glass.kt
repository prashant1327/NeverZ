package com.productivitystreak.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.GradientColors
import com.productivitystreak.ui.theme.MotionSpec
import com.productivitystreak.ui.theme.Spacing

/**
 * Glassmorphic Card Component
 * Uses a semi-transparent background with a subtle gradient border and glow.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 0.dp,
    borderGradient: Brush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.05f)
        )
    ),
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    contentPadding: PaddingValues = PaddingValues(Spacing.lg),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(shape)
            .border(
                BorderStroke(1.dp, borderGradient),
                shape
            ),
        color = containerColor,
        shape = shape,
        tonalElevation = elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * Premium Glass Card
 * Uses the Premium gradient for a more striking look.
 */
@Composable
fun PremiumGlassCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val premiumGradient = Brush.linearGradient(
        colors = listOf(
            GradientColors.PremiumStart.copy(alpha = 0.15f),
            GradientColors.PremiumEnd.copy(alpha = 0.05f)
        )
    )
    
    val borderGradient = Brush.linearGradient(
        colors = listOf(
            GradientColors.PremiumStart.copy(alpha = 0.5f),
            GradientColors.PremiumEnd.copy(alpha = 0.2f)
        )
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .border(
                BorderStroke(1.dp, borderGradient),
                RoundedCornerShape(24.dp)
            ),
        color = Color.Transparent,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .background(premiumGradient)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
            ) {
                content()
            }
        }
    }
}

/**
 * Interactive Glass Card - Glass card with press animation
 */
@Composable
fun InteractiveGlassCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 0.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    contentPadding: PaddingValues = PaddingValues(Spacing.lg),
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = MotionSpec.quickScale(),
        label = "glass-card-scale"
    )
    
    val borderGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.05f)
        )
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .border(
                BorderStroke(1.dp, borderGradient),
                shape
            ),
        color = containerColor,
        shape = shape,
        tonalElevation = elevation,
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}
