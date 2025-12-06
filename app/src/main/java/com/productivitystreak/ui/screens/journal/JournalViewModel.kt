package com.productivitystreak.ui.screens.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivitystreak.data.local.entity.JournalEntity
import com.productivitystreak.data.repository.JournalRepository
import com.productivitystreak.data.repository.ReflectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(
    private val reflectionRepository: ReflectionRepository,
    private val journalRepository: JournalRepository,
    private val geminiClient: com.productivitystreak.data.gemini.GeminiClient
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()
    
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _buddhaResponse = MutableStateFlow<String?>(null)
    val buddhaResponse: StateFlow<String?> = _buddhaResponse.asStateFlow()

    private val _realtimeFeedback = MutableStateFlow<String?>(null)
    val realtimeFeedback: StateFlow<String?> = _realtimeFeedback.asStateFlow()

    private val _isGeneratingFeedback = MutableStateFlow(false)
    val isGeneratingFeedback: StateFlow<Boolean> = _isGeneratingFeedback.asStateFlow()

    fun onSubmitJournalEntry(
        mood: Int,
        notes: String,
        highlights: String?,
        gratitude: String?,
        tomorrowGoals: String?
    ) {
        val trimmedNotes = notes.trim()
        if (trimmedNotes.isBlank()) {
            _uiMessage.value = "Journal entry can’t be empty."
            return
        }
        val safeMood = mood.coerceIn(1, 5)
        val cleanedHighlights = highlights?.trim()?.takeIf { it.isNotBlank() }
        val cleanedGratitude = gratitude?.trim()?.takeIf { it.isNotBlank() }
        val cleanedTomorrow = tomorrowGoals?.trim()?.takeIf { it.isNotBlank() }

        _isSubmitting.value = true
        viewModelScope.launch {
            try {
                reflectionRepository.saveReflection(
                    mood = safeMood,
                    notes = trimmedNotes,
                    highlights = cleanedHighlights,
                    gratitude = cleanedGratitude,
                    tomorrowGoals = cleanedTomorrow
                )
                _uiMessage.value = "Journal entry saved"
                
                // Generate Buddha's Response
                val response = geminiClient.generateJournalFeedback(trimmedNotes)
                _buddhaResponse.value = response
                
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error saving journal entry", e)
                _uiMessage.value = "Couldn’t save journal entry. Please retry."
            } finally {
                _isSubmitting.value = false
            }
        }
    }
    
    fun clearMessage() {
        _uiMessage.value = null
    }

    fun clearBuddhaResponse() {
        _buddhaResponse.value = null
    }

    /**
     * Generate real-time AI feedback as user types
     * Debounced to avoid excessive API calls
     */
    fun onJournalTextChanged(text: String) {
        if (text.trim().length < 50) {
            _realtimeFeedback.value = null
            return
        }

        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Debounce 2 seconds
            if (_isGeneratingFeedback.value) return@launch

            _isGeneratingFeedback.value = true
            try {
                val feedback = geminiClient.generateJournalFeedback(text.trim())
                _realtimeFeedback.value = feedback
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error generating real-time feedback", e)
            } finally {
                _isGeneratingFeedback.value = false
            }
        }
    }

    fun clearRealtimeFeedback() {
        _realtimeFeedback.value = null
    }

    // Stoic Journal Methods
    val recentJournalEntries: StateFlow<List<JournalEntity>> = journalRepository
        .getRecentEntries(7)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isSavingStoic = MutableStateFlow(false)
    val isSavingStoic: StateFlow<Boolean> = _isSavingStoic.asStateFlow()

    private val _stoicSaveComplete = MutableStateFlow(false)
    val stoicSaveComplete: StateFlow<Boolean> = _stoicSaveComplete.asStateFlow()

    fun onSubmitStoicJournal(
        whatDidWell: String,
        whereLackedDiscipline: String,
        whatWillDoBetter: String
    ) {
        if (whatDidWell.isBlank() && whereLackedDiscipline.isBlank() && whatWillDoBetter.isBlank()) {
            _uiMessage.value = "Please fill in at least one reflection."
            return
        }

        _isSavingStoic.value = true
        viewModelScope.launch {
            try {
                journalRepository.saveEntry(
                    whatDidWell = whatDidWell,
                    whereLackedDiscipline = whereLackedDiscipline,
                    whatWillDoBetter = whatWillDoBetter
                )
                _uiMessage.value = "Reflection saved"
                _stoicSaveComplete.value = true
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error saving stoic journal", e)
                _uiMessage.value = "Couldn't save reflection. Please retry."
            } finally {
                _isSavingStoic.value = false
            }
        }
    }

    fun resetStoicSaveComplete() {
        _stoicSaveComplete.value = false
    }
}
