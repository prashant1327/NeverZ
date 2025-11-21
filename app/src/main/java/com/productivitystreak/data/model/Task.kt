package com.productivitystreak.data.model

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val priority: TaskPriority = TaskPriority.MEDIUM
)

enum class TaskPriority {
    HIGH, MEDIUM, LOW
}
