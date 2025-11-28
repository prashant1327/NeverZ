package com.productivitystreak.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val score: Int,
    val avatarUrl: String? = null,
    val isCurrentUser: Boolean = false
)

data class LeaderboardUiState(
    val globalUsers: List<LeaderboardUser> = emptyList(),
    val friendsUsers: List<LeaderboardUser> = emptyList(),
    val currentUserRank: LeaderboardUser? = null,
    val isLoading: Boolean = false
)

class LeaderboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Mock data for now
            val mockGlobal = listOf(
                LeaderboardUser(1, "Alex Chen", 2450),
                LeaderboardUser(2, "Sarah Jones", 2340),
                LeaderboardUser(3, "Mike Ross", 2100),
                LeaderboardUser(4, "You", 1850, isCurrentUser = true),
                LeaderboardUser(5, "Jessica Pearson", 1720)
            )
            
            _uiState.value = LeaderboardUiState(
                globalUsers = mockGlobal,
                currentUserRank = mockGlobal.find { it.isCurrentUser },
                isLoading = false
            )
        }
    }
}
