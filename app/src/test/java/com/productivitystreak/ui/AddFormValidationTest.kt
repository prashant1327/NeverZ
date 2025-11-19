package com.productivitystreak.ui

import android.app.Application
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.repository.ReflectionRepository
import com.productivitystreak.data.repository.StreakRepository
import com.productivitystreak.notifications.StreakReminderScheduler
import com.productivitystreak.ui.state.UiMessageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AddFormValidationTest {

    private lateinit var application: Application
    private lateinit var quoteRepository: QuoteRepository
    private lateinit var streakRepository: StreakRepository
    private lateinit var reflectionRepository: ReflectionRepository
    private lateinit var reminderScheduler: StreakReminderScheduler
    private lateinit var preferencesManager: PreferencesManager

    private lateinit var viewModel: AppViewModel

    private val dispatcher: TestDispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        application = mock()
        whenever(application.applicationContext).thenReturn(application)

        quoteRepository = mock()
        streakRepository = mock()
        reflectionRepository = mock()
        reminderScheduler = mock()
        preferencesManager = mock()

        whenever(preferencesManager.userName).thenReturn(flowOf(""))
        whenever(preferencesManager.onboardingCompleted).thenReturn(flowOf(false))

        viewModel = AppViewModel(
            application = application,
            quoteRepository = quoteRepository,
            streakRepository = streakRepository,
            reflectionRepository = reflectionRepository,
            reminderScheduler = reminderScheduler,
            preferencesManager = preferencesManager
        )

        scope.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun newHabit_emptyName_showsError_andDoesNotHitRepository() = scope.runTest {
        viewModel.onSubmitNewHabit(
            name = "   ",
            goalPerDay = 10,
            unit = "minutes",
            category = "Focus",
            colorHex = null,
            iconName = null
        )

        advanceUntilIdle()

        verify(streakRepository, never()).createStreak(any(), any(), any(), any(), any(), any())
        assertFalse(viewModel.uiState.value.addUiState.isSubmitting)

        val message = viewModel.uiState.value.uiMessage
        assertNotNull(message)
        assertEquals(UiMessageType.ERROR, message!!.type)
        assertEquals("Habit name can’t be empty.", message.text)
    }

    @Test
    fun journalSubmission_togglesSubmitting_andShowsErrorOnFailure() = scope.runTest {
        whenever(
            reflectionRepository.saveReflection(
                mood = any(),
                notes = any(),
                highlights = any(),
                gratitude = any(),
                tomorrowGoals = any()
            )
        ).thenReturn(Unit)

        viewModel.onSubmitJournalEntry(
            mood = 3,
            notes = "Today was productive.",
            highlights = null,
            gratitude = null,
            tomorrowGoals = null
        )

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.addUiState.isSubmitting)

        whenever(
            reflectionRepository.saveReflection(
                mood = any(),
                notes = any(),
                highlights = any(),
                gratitude = any(),
                tomorrowGoals = any()
            )
        ).thenThrow(RuntimeException("db error"))

        viewModel.onSubmitJournalEntry(
            mood = 4,
            notes = "Another entry.",
            highlights = null,
            gratitude = null,
            tomorrowGoals = null
        )

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.addUiState.isSubmitting)

        val message = viewModel.uiState.value.uiMessage
        assertNotNull(message)
        assertEquals(UiMessageType.ERROR, message!!.type)
    }
}
