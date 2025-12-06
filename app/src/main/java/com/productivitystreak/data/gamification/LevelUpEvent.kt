package com.productivitystreak.data.gamification

import com.productivitystreak.data.local.entity.UserStats

/**
 * Event emitted when a user levels up after claiming XP.
 * Contains the new stats and level information for UI display.
 *
 * @property previousLevel The level before leveling up
 * @property newLevel The new level after leveling up
 * @property currentXp XP remaining after level-up (overflow XP)
 * @property xpToNextLevel XP required to reach the next level
 * @property stats The complete updated user stats
 */
data class LevelUpEvent(
    val previousLevel: Int,
    val newLevel: Int,
    val currentXp: Int,
    val xpToNextLevel: Int,
    val stats: UserStats
)
