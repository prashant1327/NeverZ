package com.productivitystreak.data.model

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val durationDays: Int,
    val iconId: String, // Icon identifier for lookup (e.g., "brain", "sword", "phone")
    val colorHex: String,
    val requiredHabits: List<String>, // List of habit categories or names
    val difficulty: String = "Medium" // Easy, Medium, Hard, Savage
)

