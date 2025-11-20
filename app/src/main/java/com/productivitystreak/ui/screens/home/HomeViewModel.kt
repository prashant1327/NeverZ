package com.productivitystreak.ui.screens.home

import androidx.lifecycle.ViewModel
import com.productivitystreak.ui.state.vocabulary.VocabularyWord
import java.time.LocalDate
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

    private val _wordOfTheDay = MutableStateFlow<VocabularyWord?>(null)
    val wordOfTheDay: StateFlow<VocabularyWord?> = _wordOfTheDay.asStateFlow()

    /**
     * Selects a deterministic "word of the day" from the provided list.
     * Call this whenever the vocabulary list is updated or on app start.
     */
    fun refreshWordForToday(words: List<VocabularyWord>) {
        if (words.isEmpty()) {
            _wordOfTheDay.value = null
            return
        }
        val todayIndex = LocalDate.now().dayOfYear % words.size
        _wordOfTheDay.value = words[todayIndex]
    }

    fun markWordKnown() {
        // Hook for persistence / analytics when a user confirms mastery.
    }

    fun startLearningWord() {
        // Hook for navigation into a detailed learning experience.
    }
}
