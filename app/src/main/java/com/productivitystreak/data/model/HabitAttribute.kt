package com.productivitystreak.data.model

enum class HabitAttribute(val displayName: String, val description: String) {
    STRENGTH("Strength", "Physical power and endurance"),
    INTELLIGENCE("Intelligence", "Mental acuity and knowledge"),
    CHARISMA("Charisma", "Social influence and charm"),
    WISDOM("Wisdom", "Insight and experience"),
    DISCIPLINE("Discipline", "Willpower and consistency"),
    NONE("None", "No specific attribute")
}
