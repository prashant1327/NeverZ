package com.productivitystreak.data.gamification

import com.productivitystreak.data.model.HabitAttribute

/**
 * Result of a stat decay check.
 * Returned when checking if user has missed Health-category streaks.
 *
 * @property decayOccurred Whether any stat decay happened
 * @property affectedStat Which stat was reduced (typically STRENGTH for Health)
 * @property previousValue The stat value before decay
 * @property newValue The stat value after decay
 * @property daysMissed Number of consecutive days the protocol was missed
 */
data class StatDecayResult(
    val decayOccurred: Boolean,
    val affectedStat: HabitAttribute?,
    val previousValue: Int?,
    val newValue: Int?,
    val daysMissed: Int
) {
    companion object {
        /** Create a result indicating no decay occurred */
        fun noDecay(): StatDecayResult = StatDecayResult(
            decayOccurred = false,
            affectedStat = null,
            previousValue = null,
            newValue = null,
            daysMissed = 0
        )
    }
}
