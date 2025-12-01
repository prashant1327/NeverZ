package com.productivitystreak.data.model

data class RpgStats(
    val strength: Int = 1,
    val intelligence: Int = 1,
    val charisma: Int = 1,
    val wisdom: Int = 1,
    val discipline: Int = 1,
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 100
) {
    fun getStat(attribute: HabitAttribute): Int {
        return when (attribute) {
            HabitAttribute.STRENGTH -> strength
            HabitAttribute.INTELLIGENCE -> intelligence
            HabitAttribute.CHARISMA -> charisma
            HabitAttribute.WISDOM -> wisdom
            HabitAttribute.DISCIPLINE -> discipline
            HabitAttribute.NONE -> 0
        }
    }
}
