package com.productivitystreak.ui.theme

import androidx.compose.ui.graphics.Color

// ==================== CORE BRAND COLORS ====================
// Premium Glassmorphism Palette

private val ElectricBlue = Color(0xFF2E86DE)
private val NeonCyan = Color(0xFF00D2D3)
private val HotPink = Color(0xFFFF9FF3)
private val DeepPurple = Color(0xFF5F27CD)

private val Obsidian = Color(0xFF121212)
private val Void = Color(0xFF000000)
private val Glass = Color(0xFF1E1E2E)
private val Graphite = Color(0xFF2F3640)
private val Silver = Color(0xFFC8D6E5)
private val Platinum = Color(0xFFF5F6FA)
private val White = Color(0xFFFFFFFF)

// ==================== LIGHT THEME COLORS ====================

val Primary = ElectricBlue
val OnPrimary = White
val PrimaryContainer = ElectricBlue.copy(alpha = 0.1f)
val OnPrimaryContainer = ElectricBlue

val Secondary = DeepPurple
val OnSecondary = White
val SecondaryContainer = DeepPurple.copy(alpha = 0.1f)
val OnSecondaryContainer = DeepPurple

val Tertiary = NeonCyan
val OnTertiary = Void
val TertiaryContainer = NeonCyan.copy(alpha = 0.1f)
val OnTertiaryContainer = Color(0xFF006C6C)

val Error = Color(0xFFEE5253)
val OnError = White
val ErrorContainer = Color(0xFFFFCDD2)
val OnErrorContainer = Color(0xFFB71C1C)

val Background = Platinum
val OnBackground = Graphite
val Surface = White
val OnSurface = Graphite
val SurfaceVariant = Color(0xFFDBE4ED)
val OnSurfaceVariant = Color(0xFF49454F)

val Outline = Color(0xFF79747E)
val OutlineVariant = Color(0xFFCAC4D0)

// ==================== DARK THEME COLORS ====================

val DarkPrimary = Color(0xFF54A0FF)
val DarkOnPrimary = Void
val DarkPrimaryContainer = Color(0xFF00497D)
val DarkOnPrimaryContainer = Color(0xFFD1E4FF)

val DarkSecondary = Color(0xFFA29BFE)
val DarkOnSecondary = Void
val DarkSecondaryContainer = Color(0xFF483D8B)
val DarkOnSecondaryContainer = Color(0xFFEADDFF)

val DarkTertiary = NeonCyan
val DarkOnTertiary = Void
val DarkTertiaryContainer = Color(0xFF004F4F)
val DarkOnTertiaryContainer = Color(0xFF9CF8F8)

val DarkError = Color(0xFFFF6B6B)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

val DarkBackground = Void
val DarkOnBackground = Silver
val DarkSurface = Glass
val DarkOnSurface = Silver
val DarkSurfaceVariant = Graphite
val DarkOnSurfaceVariant = Color(0xFFCAC4D0)

val DarkOutline = Color(0xFF938F99)
val DarkOutlineVariant = Color(0xFF49454F)

// ==================== CATEGORY/STREAK ACCENT COLORS ====================

object StreakColors {
    val Reading = Color(0xFF48DBFB) // Cyan
    val ReadingContainer = Reading.copy(alpha = 0.1f)
    val OnReadingContainer = Reading

    val Learning = Color(0xFFFECA57) // Yellow
    val LearningContainer = Learning.copy(alpha = 0.1f)
    val OnLearningContainer = Learning

    val Vocabulary = Color(0xFF5F27CD) // Purple
    val VocabularyContainer = Vocabulary.copy(alpha = 0.1f)
    val OnVocabularyContainer = Vocabulary

    val Creative = Color(0xFFFF9FF3) // Pink
    val CreativeContainer = Creative.copy(alpha = 0.1f)
    val OnCreativeContainer = Creative

    val Exercise = Color(0xFFFF6B6B) // Red
    val ExerciseContainer = Exercise.copy(alpha = 0.1f)
    val OnExerciseContainer = Exercise

    val Wellness = Color(0xFF1DD1A1) // Green
    val WellnessContainer = Wellness.copy(alpha = 0.1f)
    val OnWellnessContainer = Wellness

    val Meditation = Color(0xFF54A0FF) // Blue
    val MeditationContainer = Meditation.copy(alpha = 0.1f)
    val OnMeditationContainer = Meditation

    val Productivity = Color(0xFF00D2D3) // Teal
    val ProductivityContainer = Productivity.copy(alpha = 0.1f)
    val OnProductivityContainer = Productivity
}

// ==================== GRADIENT COLORS ====================

object GradientColors {
    // Premium Blue-Purple
    val PremiumStart = Color(0xFF2E86DE)
    val PremiumEnd = Color(0xFF5F27CD)

    // Sunset
    val SunriseStart = Color(0xFFFF9FF3)
    val SunriseEnd = Color(0xFFFECA57)

    // Ocean
    val OceanStart = Color(0xFF48DBFB)
    val OceanEnd = Color(0xFF00D2D3)

    // Success
    val SuccessStart = Color(0xFF1DD1A1)
    val SuccessEnd = Color(0xFF10AC84)
    
    // Twilight
    val TwilightStart = Color(0xFF5F27CD)
    val TwilightEnd = Color(0xFF341F97)
}

// ==================== SEMANTIC COLORS ====================

object SemanticColors {
    val Success = Color(0xFF1DD1A1)
    val OnSuccess = White
    val SuccessContainer = Color(0xFF1DD1A1).copy(alpha = 0.1f)
    val OnSuccessContainer = Color(0xFF1DD1A1)

    val Warning = Color(0xFFFECA57)
    val OnWarning = Void
    val WarningContainer = Color(0xFFFECA57).copy(alpha = 0.1f)
    val OnWarningContainer = Color(0xFFFECA57)

    val Info = Color(0xFF54A0FF)
    val OnInfo = White
    val InfoContainer = Color(0xFF54A0FF).copy(alpha = 0.1f)
    val OnInfoContainer = Color(0xFF54A0FF)
}

// ==================== UTILITY COLORS ====================

val Scrim = Color(0xFF000000)
val ScrimTransparent = Color(0x00000000)

val DividerLight = Color(0xFFE1E2EC)
val DividerDark = Color(0xFF44474F)

// ==================== MATERIAL 3 TONAL SURFACES ====================

// Light theme tonal surfaces
val SurfaceDim = Color(0xFFDED8E1)
val SurfaceBright = Color(0xFFFEF7FF)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerLow = Color(0xFFF7F2FA)
val SurfaceContainer = Color(0xFFF3EDF7)
val SurfaceContainerHigh = Color(0xFFECE6F0)
val SurfaceContainerHighest = Color(0xFFE6E0E9)

// Dark theme tonal surfaces
val DarkSurfaceDim = Color(0xFF141218)
val DarkSurfaceBright = Color(0xFF3B383E)
val DarkSurfaceContainerLowest = Color(0xFF0F0D13)
val DarkSurfaceContainerLow = Color(0xFF1D1B20)
val DarkSurfaceContainer = Color(0xFF211F26)
val DarkSurfaceContainerHigh = Color(0xFF2B2930)
val DarkSurfaceContainerHighest = Color(0xFF36343B)

// ==================== INTERACTION STATE COLORS ====================

object StateLayerOpacity {
    const val hover = 0.08f
    const val focus = 0.12f
    const val pressed = 0.12f
    const val dragged = 0.16f
}

// ==================== DESIGN COLOR TOKENS ====================

data class NeverZeroDesignColors(
    val isDark: Boolean,
    val background: Color,
    val backgroundAlt: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val glow: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryMuted: Color,
    val secondary: Color,
    val onSecondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val disabled: Color,
    val success: Color,
    val warning: Color,
    val error: Color
)

object NeverZeroDesignPalettes {
    val Dark = NeverZeroDesignColors(
        isDark = true,
        background = Void,
        backgroundAlt = Glass,
        surface = Glass,
        surfaceElevated = Graphite,
        border = Graphite,
        glow = ElectricBlue.copy(alpha = 0.3f),
        primary = ElectricBlue,
        onPrimary = White,
        primaryMuted = ElectricBlue.copy(alpha = 0.5f),
        secondary = DeepPurple,
        onSecondary = White,
        textPrimary = Silver,
        textSecondary = Color(0xFF9CA3AF),
        disabled = Color(0xFF4B5563),
        success = Color(0xFF1DD1A1),
        warning = Color(0xFFFECA57),
        error = Color(0xFFFF6B6B)
    )

    val Light = NeverZeroDesignColors(
        isDark = false,
        background = Platinum,
        backgroundAlt = White,
        surface = White,
        surfaceElevated = Color(0xFFF1F2F6),
        border = Color(0xFFDFE6E9),
        glow = ElectricBlue.copy(alpha = 0.2f),
        primary = ElectricBlue,
        onPrimary = White,
        primaryMuted = ElectricBlue.copy(alpha = 0.6f),
        secondary = DeepPurple,
        onSecondary = White,
        textPrimary = Graphite,
        textSecondary = Color(0xFF636E72),
        disabled = Color(0xFFB2BEC3),
        success = Color(0xFF10AC84),
        warning = Color(0xFFFF9F43),
        error = Color(0xFFEE5253)
    )
}

// ==================== EXTENDED SEMANTIC COLORS ====================

object ExtendedSemanticColors {
    val Focus = ElectricBlue
    val DarkFocus = Color(0xFF54A0FF)
    
    val DisabledContent = Color(0xFF1C1B1F).copy(alpha = 0.38f)
    val DisabledContainer = Color(0xFF1C1B1F).copy(alpha = 0.12f)
    val DarkDisabledContent = Color(0xFFE6E1E5).copy(alpha = 0.38f)
    val DarkDisabledContainer = Color(0xFFE6E1E5).copy(alpha = 0.12f)
    
    val LoadingShimmer = Color(0xFFE1E2EC)
    val DarkLoadingShimmer = Color(0xFF44474F)
}

// ==================== CUSTOM ACCENT COLORS ====================

object AccentColors {
    val Achievement = Color(0xFFFFD700) // Gold
    val AchievementContainer = Color(0xFFFFF8E1)
    
    val Premium = DeepPurple
    val PremiumContainer = Color(0xFFEDE7F6)
    
    val StreakFire = Color(0xFFFF6B6B)
    val StreakFireContainer = Color(0xFFFFEBEE)
    
    val Focus = NeonCyan
    val FocusContainer = Color(0xFFE0F7FA)
}

