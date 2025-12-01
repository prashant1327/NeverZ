package com.productivitystreak.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.ui.unit.dp

/**
 * Animation design tokens for NeverZero
 * 
 * Ensures consistent, smooth motion across all UI elements.
 * Use these presets instead of ad-hoc animation specs.
 */

object AnimationDurations {
    const val Instant = 100
    const val Fast = 200
    const val Normal = 300
    const val Slow = 500
    const val VerySlow = 800
}

object AnimationCurves {
    /**
     * Springy entrance animation - use for appearing elements
     * Example: Cards, dialogs, bottom sheets
     */
    val SpringyEnter = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    
    /**
     * Smooth exit animation - use for disappearing elements
     * Example: Dialogs closing, navigation back
     */
    val SmoothExit = tween<Float>(
        durationMillis = AnimationDurations.Normal,
        easing = FastOutSlowInEasing
    )
    
    /**
     * Bouncy spring - use for playful interactions
     * Example: Button presses, confetti, celebrations
     */
    val Bouncy = spring<Float>(
        dampingRatio = 0.6f,
        stiffness = Spring.StiffnessMedium
    )
    
    /**
     * Linear fade - use for opacity changes
     * Example: Loading states, skeleton screens
     */
    val LinearFade = tween<Float>(
        durationMillis = AnimationDurations.Fast,
        easing = LinearEasing
    )
    
    /**
     * Emphasized enter - Material Motion standard
     * Use for important element entrances
     */
    val EmphasizedEnter = tween<Float>(
        durationMillis = AnimationDurations.Slow,
        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    )
    
    /**
     * Emphasized exit - Material Motion standard
     * Use for important element exits
     */
    val EmphasizedExit = tween<Float>(
        durationMillis = AnimationDurations.Fast,
        easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
    )
}

/**
 * Pre-configured animation specs for common UI patterns
 */
object AnimationPresets {
    /**
     * Button press animation
     * Scales down 95% on press, springs back on release
     */
    fun buttonPress() = AnimationCurves.Bouncy
    
    /**
     * Card entrance animation
     * Fade in + slide up from bottom
     */
    fun cardEntrance() = AnimationCurves.SpringyEnter
    
    /**
     * Screen transition
     * Smooth cross-fade between screens
     */
    fun screenTransition() = tween<Float>(
        durationMillis = AnimationDurations.Normal,
        easing = FastOutSlowInEasing
    )
    
    /**
     * List item stagger delay
     * @param index Item position in list
     * @return Delay in milliseconds
     */
    fun listItemDelay(index: Int): Int {
        return (index * 50).coerceAtMost(300)
    }
    
    /**
     * Ripple effect duration
     */
    fun ripple() = tween<Float>(
        durationMillis = AnimationDurations.Normal,
        easing = LinearOutSlowInEasing
    )
}

/**
 * Gesture-based animation configurations
 */
object GestureAnimations {
    /**
     * Swipe to dismiss threshold
     */
    val SwipeThreshold = 0.3f // 30% of width
    
    /**
     * Swipe dismiss animation
     */
    val SwipeDismiss = tween<Float>(
        durationMillis = AnimationDurations.Fast,
        easing = FastOutLinearInEasing
    )
    
    /**
     * Pull to refresh resistance
     */
    val PullResistance = 0.5f
    
    /**
     * Snap back animation after release
     */
    val SnapBack = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )
}

/**
 * Celebration animations for achievements
 */
object CelebrationAnimations {
    /**
     * Confetti explosion duration
     */
    const val ConfettiDuration = 1500
    
    /**
     * Level up animation
     * Multi-stage: Glow → Scale → Confetti
     */
    fun levelUp() = spring<Float>(
        dampingRatio = 0.5f,
        stiffness = Spring.StiffnessLow
    )
    
    /**
     * Streak milestone animation
     * Pulsing glow effect
     */
    fun streakMilestone() = infiniteRepeatable<Float>(
        animation = tween(1000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
    
    /**
     * Achievement unlock
     * Dramatic entrance with overshoot
     */
    fun achievementUnlock() = spring<Float>(
        dampingRatio = 0.4f,
        stiffness = Spring.StiffnessMediumLow
    )
}

/**
 * Loading & skeleton animations
 */
object LoadingAnimations {
    /**
     * Shimmer effect for skeleton screens
     */
    fun shimmer() = infiniteRepeatable<Float>(
        animation = tween(1500, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
    
    /**
     * Circular progress indicator
     */
    fun circularProgress() = infiniteRepeatable<Float>(
        animation = tween(1000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
    
    /**
     * Pulse animation for loading states
     */
    fun pulse() = infiniteRepeatable<Float>(
        animation = tween(800, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
}

/**
 * Parallax scroll offsets for depth effects
 */
object ParallaxOffsets {
    val Background = 0.5f  // Moves at 50% of scroll speed
    val Midground = 0.75f  // Moves at 75% of scroll speed
    val Foreground = 1.0f  // Moves at 100% (normal) scroll speed
}
