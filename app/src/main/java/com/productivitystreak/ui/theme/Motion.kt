package com.productivitystreak.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material 3 Motion System
 * Standardized animations and transitions following Material Design guidelines
 */

// ==================== DURATION TOKENS ====================
/**
 * Animation durations in milliseconds
 * Following Material 3 motion specifications
 */
object Duration {
    const val instant = 50          // Immediate feedback
    const val short1 = 50           // Very quick transitions
    const val short2 = 100          // Quick transitions
    const val short3 = 150          // Short transitions
    const val short4 = 200          // Default short duration
    const val medium1 = 250         // Medium transitions
    const val medium2 = 300         // Standard medium duration
    const val medium3 = 350         // Longer medium transitions
    const val medium4 = 400         // Extended medium duration
    const val long1 = 450           // Long transitions
    const val long2 = 500           // Extended long duration
    const val long3 = 550           // Longer transitions
    const val long4 = 600           // Maximum standard duration
    const val extraLong1 = 700      // Extra long transitions
    const val extraLong2 = 800      // Very long transitions
    const val extraLong3 = 900      // Extended long transitions
    const val extraLong4 = 1000     // Maximum duration
}

// ==================== EASING TOKENS ====================
/**
 * Material 3 Easing Curves
 * Standard easing functions for different motion patterns
 */
object Easing {
    /**
     * Standard easing - Most common, used for entering and exiting
     */
    val standard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    
    /**
     * Standard accelerate - Elements exiting the screen
     */
    val standardAccelerate = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
    
    /**
     * Standard decelerate - Elements entering the screen
     */
    val standardDecelerate = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)
    
    /**
     * Emphasized easing - Important or complex transitions
     */
    val emphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    
    /**
     * Emphasized accelerate - Important elements exiting
     */
    val emphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    
    /**
     * Emphasized decelerate - Important elements entering
     */
    val emphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    
    /**
     * Legacy easing - Compatibility with older animations
     */
    val legacy = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    
    /**
     * Legacy accelerate - Quick exit motion
     */
    val legacyAccelerate = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    
    /**
     * Legacy decelerate - Gentle entrance
     */
    val legacyDecelerate = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
}

// ==================== ANIMATION SPECS ====================
/**
 * Pre-configured animation specifications for common use cases
 */
object MotionSpec {
    /**
     * Quick fade animation for immediate feedback
     */
    fun <T> quickFade() = tween<T>(
        durationMillis = Duration.short2,
        easing = Easing.standard
    )
    
    /**
     * Standard fade for general purpose transitions
     */
    fun <T> fade() = tween<T>(
        durationMillis = Duration.medium2,
        easing = Easing.standard
    )
    
    /**
     * Slow fade for emphasis
     */
    fun <T> slowFade() = tween<T>(
        durationMillis = Duration.long2,
        easing = Easing.emphasized
    )
    
    /**
     * Quick scale animation for buttons and interactive elements
     */
    fun <T> quickScale() = tween<T>(
        durationMillis = Duration.short3,
        easing = Easing.standardDecelerate
    )
    
    /**
     * Standard scale for general scaling transitions
     */
    fun <T> scale() = tween<T>(
        durationMillis = Duration.medium2,
        easing = Easing.emphasized
    )
    
    /**
     * Bounce animation using spring physics
     */
    fun <T> bounce() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    
    /**
     * Elastic bounce for success states and celebrations
     */
    fun <T> elasticBounce() = spring<T>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    /**
     * Gentle spring for smooth, natural motion
     */
    fun <T> gentleSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    /**
     * Snappy spring for responsive interactions
     */
    fun <T> snappySpring() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    /**
     * Slide in from bottom (entering screen)
     */
    fun <T> slideIn() = tween<T>(
        durationMillis = Duration.medium3,
        easing = Easing.emphasizedDecelerate
    )
    
    /**
     * Slide out to bottom (exiting screen)
     */
    fun <T> slideOut() = tween<T>(
        durationMillis = Duration.medium2,
        easing = Easing.emphasizedAccelerate
    )
    
    /**
     * Expand animation for revealing content
     */
    fun <T> expand() = tween<T>(
        durationMillis = Duration.medium4,
        easing = Easing.emphasizedDecelerate
    )
    
    /**
     * Collapse animation for hiding content
     */
    fun <T> collapse() = tween<T>(
        durationMillis = Duration.medium2,
        easing = Easing.emphasizedAccelerate
    )
}

// ==================== MOTION PATTERNS ====================
/**
 * Common motion patterns and their recommended specifications
 */
object MotionPattern {
    // Container transform durations
    const val containerTransform = Duration.medium4
    
    // Shared axis durations
    const val sharedAxisX = Duration.medium2
    const val sharedAxisY = Duration.medium2
    const val sharedAxisZ = Duration.medium3
    
    // Fade through duration
    const val fadeThrough = Duration.medium2
    
    // Fade duration
    const val fade = Duration.short4
    
    // List item stagger delay
    const val staggerDelay = Duration.short1
}

// ==================== STATE LAYER ANIMATION ====================
/**
 * State layer animations for interactive components
 */
object StateLayer {
    // Press animation duration
    const val pressDuration = Duration.short1
    
    // Hover/focus animation duration
    const val hoverDuration = Duration.short4
    
    // Ripple animation duration
    const val rippleDuration = Duration.medium2
}

// ==================== SCROLL BEHAVIOR ====================
/**
 * Scroll-related animation parameters
 */
object ScrollMotion {
    // Smooth scroll duration
    const val smoothScrollDuration = Duration.medium3
    
    // Snap scroll duration
    const val snapScrollDuration = Duration.short4
    
    // Fling decay rate
    const val flingDecay = 0.35f
}
