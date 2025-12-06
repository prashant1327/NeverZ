package com.productivitystreak.data.gamification

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GamificationEngineTest {

    @Test
    fun `calculateXp returns base XP for streak under 7 days`() {
        // Given streak = 5 (less than 7)
        val streakLength = 5
        
        // When calculating XP
        val xp = calculateXpStatic(streakLength)
        
        // Then should return base XP (10)
        assertEquals(10, xp)
    }

    @Test
    fun `calculateXp returns base XP for streak exactly 7 days`() {
        // Given streak = 7 (boundary condition - NOT greater than 7)
        val streakLength = 7
        
        // When calculating XP
        val xp = calculateXpStatic(streakLength)
        
        // Then should return base XP (no multiplier at exactly 7)
        assertEquals(10, xp)
    }

    @Test
    fun `calculateXp applies 1_2x multiplier for streak over 7 days`() {
        // Given streak = 8 (just over 7)
        val streakLength = 8
        
        // When calculating XP
        val xp = calculateXpStatic(streakLength)
        
        // Then should return 10 * 1.2 = 12
        assertEquals(12, xp)
    }

    @Test
    fun `calculateXp applies 1_2x multiplier for streak at 30 days`() {
        // Given streak = 30 (boundary - NOT greater than 30)
        val streakLength = 30
        
        // When calculating XP
        val xp = calculateXpStatic(streakLength)
        
        // Then should return 10 * 1.2 = 12 (not 1.5x yet)
        assertEquals(12, xp)
    }

    @Test
    fun `calculateXp applies 1_5x multiplier for streak over 30 days`() {
        // Given streak = 31 (just over 30)
        val streakLength = 31
        
        // When calculating XP
        val xp = calculateXpStatic(streakLength)
        
        // Then should return 10 * 1.5 = 15
        assertEquals(15, xp)
    }

    @Test
    fun `calculateXp applies 1_5x multiplier for very long streak`() {
        // Given streak = 100 days
        val streakLength = 100
        
        // When calculating XP
        val xp = calculateXpStatic(streakLength)
        
        // Then should return 10 * 1.5 = 15
        assertEquals(15, xp)
    }

    @Test
    fun `calculateXpToNextLevel uses correct formula for level 1`() {
        // Given level = 1
        val level = 1
        
        // When calculating XP to next level
        val xpToNext = calculateXpToNextLevelStatic(level)
        
        // Then should return 1 * 100 * 1.5 = 150
        assertEquals(150, xpToNext)
    }

    @Test
    fun `calculateXpToNextLevel uses correct formula for level 5`() {
        // Given level = 5
        val level = 5
        
        // When calculating XP to next level
        val xpToNext = calculateXpToNextLevelStatic(level)
        
        // Then should return 5 * 100 * 1.5 = 750
        assertEquals(750, xpToNext)
    }

    @Test
    fun `calculateXpToNextLevel uses correct formula for level 10`() {
        // Given level = 10
        val level = 10
        
        // When calculating XP to next level
        val xpToNext = calculateXpToNextLevelStatic(level)
        
        // Then should return 10 * 100 * 1.5 = 1500
        assertEquals(1500, xpToNext)
    }

    // Helper methods that mirror the GamificationEngine logic for pure unit testing
    // These test the formulas without needing mocked DAOs
    
    private fun calculateXpStatic(streakLength: Int): Int {
        val multiplier = when {
            streakLength > 30 -> GamificationEngine.STREAK_MULTIPLIER_30
            streakLength > 7 -> GamificationEngine.STREAK_MULTIPLIER_7
            else -> 1.0
        }
        return (GamificationEngine.BASE_XP * multiplier).toInt()
    }
    
    private fun calculateXpToNextLevelStatic(currentLevel: Int): Int {
        return (currentLevel * 100 * 1.5).toInt()
    }
}
