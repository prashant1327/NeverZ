package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.productivitystreak.data.model.Streak
import com.productivitystreak.data.model.StreakDayRecord
import com.productivitystreak.data.model.StreakDifficulty
import com.productivitystreak.data.model.StreakFrequency

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey val id: String,
    val name: String,
    val currentCount: Int,
    val longestCount: Int,
    val goalPerDay: Int,
    val unit: String,
    val category: String,
    val history: List<StreakDayRecord>,
    val color: String,
    val icon: String,
    val frequency: StreakFrequency,
    val targetPerPeriod: Int?,
    val customDaysOfWeek: List<String>,
    val reminderEnabled: Boolean,
    val reminderTime: String,
    val difficulty: StreakDifficulty,
    val allowFreezeDays: Boolean,
    val rescuedDates: List<String>,
    val freezeDaysAvailable: Int,
    val freezeDaysUsed: Int,
    val lastUpdated: Long,
    val isArchived: Boolean
)

fun StreakEntity.toStreak(): Streak {
    return Streak(
        id = id,
        name = name,
        currentCount = currentCount,
        longestCount = longestCount,
        goalPerDay = goalPerDay,
        unit = unit,
        category = category,
        history = history,
        color = color,
        icon = icon,
        frequency = frequency,
        targetPerPeriod = targetPerPeriod,
        customDaysOfWeek = customDaysOfWeek,
        reminderEnabled = reminderEnabled,
        reminderTime = reminderTime,
        difficulty = difficulty,
        allowFreezeDays = allowFreezeDays,
        rescuedDates = rescuedDates,
        freezeDaysAvailable = freezeDaysAvailable,
        freezeDaysUsed = freezeDaysUsed,
        lastUpdated = lastUpdated,
        isArchived = isArchived
    )
}

fun Streak.toEntity(): StreakEntity {
    return StreakEntity(
        id = id,
        name = name,
        currentCount = currentCount,
        longestCount = longestCount,
        goalPerDay = goalPerDay,
        unit = unit,
        category = category,
        history = history,
        color = color,
        icon = icon,
        frequency = frequency,
        targetPerPeriod = targetPerPeriod,
        customDaysOfWeek = customDaysOfWeek,
        reminderEnabled = reminderEnabled,
        reminderTime = reminderTime,
        difficulty = difficulty,
        allowFreezeDays = allowFreezeDays,
        rescuedDates = rescuedDates,
        freezeDaysAvailable = freezeDaysAvailable,
        freezeDaysUsed = freezeDaysUsed,
        lastUpdated = lastUpdated,
        isArchived = isArchived
    )
}
