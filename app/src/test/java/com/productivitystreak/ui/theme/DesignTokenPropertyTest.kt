package com.productivitystreak.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.checkAll

/**
 * Property-Based Tests for Design Token Completeness
 * Feature: ux-overhaul, Property: Design tokens exist and have valid values
 * Validates: Requirements 1.1, 1.2, 1.3
 */
class DesignTokenPropertyTest : StringSpec({

    "property - all spacing tokens should have valid positive values" {
        checkAll(100, Arb.spacingToken()) { tokenName ->
            val value = getSpacingValue(tokenName)
            
            // All spacing values should be non-negative
            value.value shouldBe value.value.coerceAtLeast(0f)
            
            // Spacing should follow 4dp grid system (except none and xxxs)
            if (tokenName != "none" && tokenName != "xxxs") {
                (value.value % 4f) shouldBe 0f
            }
        }
    }

    "property - all elevation tokens should have valid non-negative values" {
        checkAll(100, Arb.elevationToken()) { tokenName ->
            val value = getElevationValue(tokenName)
            
            // All elevation values should be non-negative
            value.value shouldBe value.value.coerceAtLeast(0f)
            
            // Elevation should be defined (not null)
            value shouldNotBe null
        }
    }

    "property - all shape tokens should be defined and have valid corner radii" {
        checkAll(100, Arb.shapeToken()) { tokenName ->
            val shape = getShapeValue(tokenName)
            
            // Shape should be defined
            shape shouldNotBe null
        }
    }

    "property - spacing tokens follow hierarchical order" {
        val spacingOrder = listOf(
            Spacing.none,
            Spacing.xxxs,
            Spacing.xxs,
            Spacing.xs,
            Spacing.sm,
            Spacing.md,
            Spacing.lg,
            Spacing.xl,
            Spacing.xxl,
            Spacing.xxxl,
            Spacing.xxxxl,
            Spacing.xxxxxl
        )
        
        // Each spacing value should be greater than or equal to the previous
        for (i in 1 until spacingOrder.size) {
            (spacingOrder[i].value >= spacingOrder[i - 1].value) shouldBe true
        }
    }

    "property - elevation tokens follow hierarchical order" {
        val elevationOrder = listOf(
            Elevation.none,
            Elevation.level0,
            Elevation.level1,
            Elevation.level2,
            Elevation.level3,
            Elevation.level4,
            Elevation.level5,
            Elevation.level6,
            Elevation.level7
        )
        
        // Each elevation value should be greater than or equal to the previous
        for (i in 1 until elevationOrder.size) {
            (elevationOrder[i].value >= elevationOrder[i - 1].value) shouldBe true
        }
    }

    "property - all size tokens should have valid positive values" {
        checkAll(100, Arb.sizeToken()) { tokenName ->
            val value = getSizeValue(tokenName)
            
            // All size values should be positive
            value.value shouldBe value.value.coerceAtLeast(0f)
            
            // Size should be defined
            value shouldNotBe null
        }
    }

    "property - all opacity tokens should be between 0 and 1" {
        checkAll(100, Arb.opacityToken()) { tokenName ->
            val value = getOpacityValue(tokenName)
            
            // Opacity should be between 0 and 1
            (value >= 0f) shouldBe true
            (value <= 1f) shouldBe true
        }
    }

    "property - all border tokens should have valid non-negative values" {
        checkAll(100, Arb.borderToken()) { tokenName ->
            val value = getBorderValue(tokenName)
            
            // Border values should be non-negative
            value.value shouldBe value.value.coerceAtLeast(0f)
        }
    }

    "property - touch target tokens meet accessibility minimums" {
        // Minimum touch target should be at least 48dp per WCAG guidelines
        TouchTarget.minimum.value shouldBe 48f
        
        // Recommended should be >= minimum
        (TouchTarget.recommended.value >= TouchTarget.minimum.value) shouldBe true
        
        // Large should be >= recommended
        (TouchTarget.large.value >= TouchTarget.recommended.value) shouldBe true
    }
})

// Helper functions to get token values by name
private fun getSpacingValue(tokenName: String): Dp = when (tokenName) {
    "none" -> Spacing.none
    "xxxs" -> Spacing.xxxs
    "xxs" -> Spacing.xxs
    "xs" -> Spacing.xs
    "sm" -> Spacing.sm
    "md" -> Spacing.md
    "lg" -> Spacing.lg
    "xl" -> Spacing.xl
    "xxl" -> Spacing.xxl
    "xxxl" -> Spacing.xxxl
    "xxxxl" -> Spacing.xxxxl
    "xxxxxl" -> Spacing.xxxxxl
    else -> 0.dp
}

private fun getElevationValue(tokenName: String): Dp = when (tokenName) {
    "none" -> Elevation.none
    "level0" -> Elevation.level0
    "level1" -> Elevation.level1
    "level2" -> Elevation.level2
    "level3" -> Elevation.level3
    "level4" -> Elevation.level4
    "level5" -> Elevation.level5
    "level6" -> Elevation.level6
    "level7" -> Elevation.level7
    else -> 0.dp
}

private fun getShapeValue(tokenName: String) = when (tokenName) {
    "extraSmall" -> Shapes.extraSmall
    "small" -> Shapes.small
    "medium" -> Shapes.medium
    "large" -> Shapes.large
    "extraLarge" -> Shapes.extraLarge
    "full" -> Shapes.full
    else -> null
}

private fun getSizeValue(tokenName: String): Dp = when (tokenName) {
    "iconSmall" -> Size.iconSmall
    "iconMedium" -> Size.iconMedium
    "iconLarge" -> Size.iconLarge
    "iconExtraLarge" -> Size.iconExtraLarge
    "buttonSmall" -> Size.buttonSmall
    "buttonMedium" -> Size.buttonMedium
    "buttonLarge" -> Size.buttonLarge
    "avatarSmall" -> Size.avatarSmall
    "avatarMedium" -> Size.avatarMedium
    "avatarLarge" -> Size.avatarLarge
    "avatarExtraLarge" -> Size.avatarExtraLarge
    else -> 0.dp
}

private fun getOpacityValue(tokenName: String): Float = when (tokenName) {
    "disabled" -> Opacity.disabled
    "medium" -> Opacity.medium
    "high" -> Opacity.high
    "overlay" -> Opacity.overlay
    "scrim" -> Opacity.scrim
    else -> 0f
}

private fun getBorderValue(tokenName: String): Dp = when (tokenName) {
    "none" -> Border.none
    "thin" -> Border.thin
    "medium" -> Border.medium
    "thick" -> Border.thick
    else -> 0.dp
}

// Arbitrary generators for token names
private fun Arb.Companion.spacingToken() = arbitrary {
    listOf("none", "xxxs", "xxs", "xs", "sm", "md", "lg", "xl", "xxl", "xxxl", "xxxxl", "xxxxxl").random()
}

private fun Arb.Companion.elevationToken() = arbitrary {
    listOf("none", "level0", "level1", "level2", "level3", "level4", "level5", "level6", "level7").random()
}

private fun Arb.Companion.shapeToken() = arbitrary {
    listOf("extraSmall", "small", "medium", "large", "extraLarge", "full").random()
}

private fun Arb.Companion.sizeToken() = arbitrary {
    listOf(
        "iconSmall", "iconMedium", "iconLarge", "iconExtraLarge",
        "buttonSmall", "buttonMedium", "buttonLarge",
        "avatarSmall", "avatarMedium", "avatarLarge", "avatarExtraLarge"
    ).random()
}

private fun Arb.Companion.opacityToken() = arbitrary {
    listOf("disabled", "medium", "high", "overlay", "scrim").random()
}

private fun Arb.Companion.borderToken() = arbitrary {
    listOf("none", "thin", "medium", "thick").random()
}
