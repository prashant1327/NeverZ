package com.productivitystreak.ui.screens.home

import androidx.lifecycle.ViewModel
import com.productivitystreak.ui.state.home.ContentType
import com.productivitystreak.ui.state.home.DailyContent
import java.time.LocalDate
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel backing the Home screen vocabulary teacher.
 *
 * Exposes a [StateFlow] of [VocabularyWord] that is stable for the current
 * day and can be refreshed when the vocabulary list changes.
 */
class HomeViewModel : ViewModel() {

    private val buddhaRepository = com.productivitystreak.data.ai.BuddhaRepository()
    
    private val _buddhaInsightState = MutableStateFlow<com.productivitystreak.data.ai.BuddhaInsightState>(com.productivitystreak.data.ai.BuddhaInsightState.Loading)
    val buddhaInsightState: StateFlow<com.productivitystreak.data.ai.BuddhaInsightState> = _buddhaInsightState.asStateFlow()

    init {
        // In a real app, this would come from a repository
        loadDailyContent()
    }
    
    fun loadBuddhaInsight(streaks: List<com.productivitystreak.data.model.Streak>) {
        if (streaks.isEmpty()) return
        
        // Don't reload if we already have a success state to avoid unnecessary API calls
        if (_buddhaInsightState.value is com.productivitystreak.data.ai.BuddhaInsightState.Success) return
        
        _buddhaInsightState.value = com.productivitystreak.data.ai.BuddhaInsightState.Loading
        
        androidx.lifecycle.viewModelScope.launch {
            val result = buddhaRepository.getInsightForStreaks(streaks)
            result.fold(
                onSuccess = { insight ->
                    _buddhaInsightState.value = com.productivitystreak.data.ai.BuddhaInsightState.Success(insight)
                },
                onFailure = { error ->
                    _buddhaInsightState.value = com.productivitystreak.data.ai.BuddhaInsightState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
    
    private val _dailyContent = MutableStateFlow<DailyContent?>(null)
    val dailyContent: StateFlow<DailyContent?> = _dailyContent.asStateFlow()
    
    private val _sidequest = MutableStateFlow<com.productivitystreak.data.ai.BuddhaQuest?>(null)
    val sidequest: StateFlow<com.productivitystreak.data.ai.BuddhaQuest?> = _sidequest.asStateFlow()

    init {
        loadBuddhaWisdom()
        loadSidequest()
    }
    
    private fun loadBuddhaWisdom() {
        androidx.lifecycle.viewModelScope.launch {
            val result = buddhaRepository.getDailyWisdom()
            result.onSuccess { wisdom ->
                _dailyContent.value = DailyContent(
                    id = "buddha_wisdom_${System.currentTimeMillis()}",
                    type = if (wisdom.type == com.productivitystreak.data.ai.WisdomType.WORD) ContentType.VOCABULARY else ContentType.PHILOSOPHY,
                    title = wisdom.content.lowercase(),
                    subtitle = wisdom.origin ?: "unknown origin",
                    content = wisdom.meaning.lowercase(),
                    actionLabel = "internalize"
                )
            }
        }
    }
    
    private fun loadSidequest() {
        androidx.lifecycle.viewModelScope.launch {
            val result = buddhaRepository.generateSidequest()
            result.onSuccess { quest ->
                _sidequest.value = quest
            }
        }
    }

    fun onContentAction(content: DailyContent) {
        // Handle action (e.g., mark as collected, open details)
    }
}
