package com.productivitystreak.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.productivitystreak.ui.theme.AnimationCurves
import com.productivitystreak.ui.theme.AnimationPresets
import kotlinx.coroutines.delay

/**
 * Animated components and modifiers for NeverZero
 * 
 * Use these instead of raw Compose animations for consistency
 */

/**
 * Modifier that adds a press animation to any composable
 * Scales down on press, springs back on release
 * 
 * @param enabled Whether the press animation is active
 * @param scaleDown The scale factor when pressed (0.95 = 95%)
 * @param enableHaptic Whether to trigger haptic feedback
 */
fun Modifier.pressAnimation(
    enabled: Boolean = true,
    scaleDown: Float = 0.95f,
    enableHaptic: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this
    
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = AnimationPresets.buttonPress(),
        label = "press-scale"
    )
    
    val haptics = if (enableHaptic) LocalHapticFeedback.current else null
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    haptics?.performHapticFeedback(HapticFeedbackType.LongPress)
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

/**
 * Animated clickable that combines press animation + haptic feedback
 * 
 * @param onClick Click callback
 * @param enabled Whether the click is enabled
 * @param enableHaptic Whether to trigger haptics
 */
fun Modifier.animatedClickable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    enableHaptic: Boolean = true
): Modifier = composed {
    this
        .pressAnimation(enabled = enabled, enableHaptic = enableHaptic)
        .clickable(
            enabled = enabled,
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        )
}

/**
 * Staggered fade-in animation for list items
 * Each item fades in with a delay based on its index
 * 
 * @param index Position in the list
 * @param content The composable content
 */
@Composable
fun StaggeredListItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(AnimationPresets.listItemDelay(index).toLong())
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(AnimationCurves.SpringyEnter) + slideInVertically(
            animationSpec = AnimationCurves.SpringyEnter,
            initialOffsetY = { it / 4 }
        ),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Bouncy card entrance animation
 * Card scales up and fades in with a spring
 */
@Composable
fun AnimatedCard(
    visible: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = AnimationCurves.SpringyEnter,
            initialScale = 0.8f
        ) + fadeIn(AnimationCurves.SpringyEnter),
        exit = scaleOut(
            animationSpec = AnimationCurves.SmoothExit,
            targetScale = 0.8f
        ) + fadeOut(AnimationCurves.SmoothExit),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Shimmer loading effect for skeleton screens
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer-alpha"
    )
    
    Box(
        modifier = modifier.graphicsLayer { this.alpha = alpha }
    ) {
        content()
    }
}

/**
 * Pulsing animation for loading or attention-grabbing elements
 */
@Composable
fun PulsingElement(
    modifier: Modifier = Modifier,
    minScale: Float = 0.9f,
    maxScale: Float = 1.1f,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse-scale"
    )
    
    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}

/**
 * Celebration animation for achievements
 * Scales up dramatically with overshoot
 */
@Composable
fun CelebrationAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = 0.4f,
                stiffness = Spring.StiffnessLow
            ),
            initialScale = 0.3f
        ) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Swipe to dismiss gesture wrapper
 * Allows swiping a composable left/right to dismiss
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SwipeToDismiss(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "swipe-offset"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = animatedOffsetX
                alpha = 1f - (animatedOffsetX.absoluteValue / 1000f).coerceIn(0f, 1f)
            }
    ) {
        content()
    }
}

private fun Float.absoluteValue(): Float = if (this < 0) -this else this
