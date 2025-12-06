package com.productivitystreak.data.gamification

import com.productivitystreak.data.local.dao.StreakDao
import com.productivitystreak.data.local.dao.UserStatsDao
import com.productivitystreak.data.local.entity.UserStats
import com.productivitystreak.data.local.entity.UserStatsEntity
import com.productivitystreak.data.local.entity.toUserStats
import com.productivitystreak.data.model.HabitAttribute

/**
 * Core gamification logic engine.
 * 
 * Implements:
 * - XP calculation with streak-based multipliers
 * - Dynamic leveling formula
 * - Stat decay for missed Health protocols
 */
class GamificationEngine(
    private val userStatsDao: UserStatsDao,
    private val streakDao: StreakDao
) {
    companion object {
        /** Base XP awarded per completion */
        const val BASE_XP = 10
        
        /** Multiplier applied when streak > 7 days */
        const val STREAK_MULTIPLIER_7 = 1.2
        
        /** Multiplier applied when streak > 30 days */
        const val STREAK_MULTIPLIER_30 = 1.5
        
        /** Days missed before stat decay triggers */
        const val DECAY_THRESHOLD_DAYS = 3
        
        /** Category tag that triggers Strength decay */
        const val HEALTH_CATEGORY = "Health"
        
        /** Stat point lost per decay event */
        const val DECAY_AMOUNT = 1
    }

    /**
     * Calculate XP reward based on current streak length.
     * 
     * Formula:
     * - Base XP = 10
     * - If streak > 7 days: 1.2x multiplier
     * - If streak > 30 days: 1.5x multiplier
     *
     * @param streakLength Current consecutive days in the streak
     * @return Calculated XP amount
     */
    fun calculateXp(streakLength: Int): Int {
        val multiplier = when {
            streakLength > 30 -> STREAK_MULTIPLIER_30
            streakLength > 7 -> STREAK_MULTIPLIER_7
            else -> 1.0
        }
        return (BASE_XP * multiplier).toInt()
    }

    /**
     * Calculate XP required to reach the next level.
     * 
     * Formula: XP_TO_NEXT_LEVEL = CurrentLevel * 100 * 1.5
     *
     * @param currentLevel The user's current level
     * @return XP needed to level up
     */
    fun calculateXpToNextLevel(currentLevel: Int): Int {
        return (currentLevel * 100 * 1.5).toInt()
    }

    /**
     * Award XP for completing a streak and handle level-up logic.
     *
     * @param streakId ID of the streak being completed
     * @return LevelUpEvent if user leveled up, null otherwise
     */
    suspend fun awardXp(streakId: String): LevelUpEvent? {
        // Fetch current stats or initialize defaults
        val currentStats = userStatsDao.getStats() ?: run {
            userStatsDao.upsert(UserStatsEntity())
            userStatsDao.getStats()!!
        }

        // Get streak to determine multiplier
        val streak = streakDao.getStreakById(streakId)
        val streakLength = streak?.currentCount ?: 0
        
        // Calculate XP with streak multiplier
        val xpGained = calculateXp(streakLength)
        val newTotalXp = currentStats.currentXp + xpGained
        val xpThreshold = calculateXpToNextLevel(currentStats.level)

        return if (newTotalXp >= xpThreshold) {
            // Level up!
            val previousLevel = currentStats.level
            val newLevel = previousLevel + 1
            val overflowXp = newTotalXp - xpThreshold
            val newXpThreshold = calculateXpToNextLevel(newLevel)

            // Update database with new level, XP, and threshold
            userStatsDao.updateLevelAndXp(newLevel, overflowXp)

            // Fetch updated stats for the event
            val updatedStats = userStatsDao.getStats()?.toUserStats() ?: UserStats.default()

            LevelUpEvent(
                previousLevel = previousLevel,
                newLevel = newLevel,
                currentXp = overflowXp,
                xpToNextLevel = newXpThreshold,
                stats = updatedStats
            )
        } else {
            // No level up, just update XP
            userStatsDao.updateXp(newTotalXp)
            null
        }
    }

    /**
     * Check for and apply stat decay for missed Health protocols.
     * 
     * If a streak with category "Health" hasn't been updated in 3+ days,
     * reduce Strength by 1 (minimum 1).
     *
     * @return StatDecayResult describing what happened
     */
    suspend fun checkStatDecay(): StatDecayResult {
        val currentStats = userStatsDao.getStats() ?: return StatDecayResult.noDecay()
        
        // Get Health category streaks
        val healthStreaks = streakDao.getStreaksByCategorySync(HEALTH_CATEGORY)
        
        if (healthStreaks.isEmpty()) {
            return StatDecayResult.noDecay()
        }

        // Check for any streak not updated in DECAY_THRESHOLD_DAYS
        val currentTime = System.currentTimeMillis()
        val decayThresholdMs = DECAY_THRESHOLD_DAYS * 24 * 60 * 60 * 1000L
        
        val missedStreak = healthStreaks.find { streak ->
            (currentTime - streak.lastUpdated) > decayThresholdMs
        }

        return if (missedStreak != null) {
            val daysMissed = ((currentTime - missedStreak.lastUpdated) / (24 * 60 * 60 * 1000L)).toInt()
            val previousStrength = currentStats.strength
            val newStrength = maxOf(1, previousStrength - DECAY_AMOUNT)

            if (previousStrength > 1) {
                // Apply decay
                userStatsDao.incrementStats(str = -DECAY_AMOUNT)
                
                StatDecayResult(
                    decayOccurred = true,
                    affectedStat = HabitAttribute.STRENGTH,
                    previousValue = previousStrength,
                    newValue = newStrength,
                    daysMissed = daysMissed
                )
            } else {
                // Already at minimum, no decay possible
                StatDecayResult.noDecay()
            }
        } else {
            StatDecayResult.noDecay()
        }
    }
}
