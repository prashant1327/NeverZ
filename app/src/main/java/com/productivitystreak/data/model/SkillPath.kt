package com.productivitystreak.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class SkillPath(
    val id: String,
    val name: String,
    val description: String,
    val category: String, // Links to Streak.category
    val levels: List<Badge>,
    val colorHex: String
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String, // Key to look up icon in AppIcons or similar
    val requirementType: BadgeRequirementType,
    val requirementValue: Int,
    val isSecret: Boolean = false
)

enum class BadgeRequirementType {
    TOTAL_DAYS,
    STREAK_LENGTH,
    CONSISTENCY_SCORE
}

data class UserBadge(
    val badgeId: String,
    val earnedDateMillis: Long
)

// Helper to calculate progress
data class SkillPathProgress(
    val path: SkillPath,
    val currentLevelIndex: Int, // -1 if not started
    val nextBadge: Badge?,
    val progressToNext: Float, // 0.0 to 1.0
    val earnedBadges: List<UserBadge>
)
