package com.productivitystreak.data.ai

/**
 * Domain model for Buddha's insight
 */
data class BuddhaInsight(
    val message: String,
    val streakContext: StreakContext
)

data class StreakContext(
    val hasBrokenStreak: Boolean,
    val hasHighStreak: Boolean,
    val highestStreak: Int,
    val totalStreaks: Int
)

/**
 * Domain model for Buddha's wisdom (Word of the Day or Proverb)
 */
data class BuddhaWisdom(
    val type: WisdomType,
    val content: String, // The word or proverb
    val meaning: String, // Definition or explanation
    val origin: String? = null // e.g., "Latin", "Seneca"
)

enum class WisdomType {
    WORD,
    PROVERB,
    PHILOSOPHY
}

/**
 * Domain model for a Sidequest (Mini-Challenge)
 */
data class BuddhaQuest(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: String, // "Novice", "Adept", "Master"
    val xpReward: Int
)

/**
 * UI state for Buddha insights
 */
sealed class BuddhaInsightState {
    object Loading : BuddhaInsightState()
    data class Success(val insight: BuddhaInsight) : BuddhaInsightState()
    data class Error(val message: String) : BuddhaInsightState()
}
