package com.productivitystreak.data.model

data class UserChallenge(
    val challengeId: String,
    val startDate: Long,
    val currentDay: Int,
    val status: ChallengeStatus = ChallengeStatus.ACTIVE
)

enum class ChallengeStatus {
    ACTIVE, COMPLETED, FAILED
}
