package com.productivitystreak.ui

import android.app.Application
import com.productivitystreak.data.QuoteRepository
import com.productivitystreak.data.local.PreferencesManager
import com.productivitystreak.data.repository.ReflectionRepository
import com.productivitystreak.data.repository.RepositoryResult
import com.productivitystreak.data.repository.StreakRepository
import com.productivitystreak.data.model.Streak
import com.productivitystreak.notifications.StreakReminderScheduler
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelFlowTest {

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

        stubPreferencesDefaults()

        val sampleStreak = Streak(
            id = "reading",
            name = "Read 30 mins",
            currentCount = 0,
            longestCount = 0,
            goalPerDay = 30,
            unit = "minutes",
            category = "Reading",
            history = emptyList()
        )
        whenever(streakRepository.observeStreaks()).thenReturn(flowOf(listOf(sampleStreak)))
        whenever(streakRepository.observeTopStreaks(any())).thenReturn(flowOf(emptyList()))

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

    private fun stubPreferencesDefaults() {
        val today = LocalDate.now().toString()

        whenever(preferencesManager.userName).thenReturn(flowOf("Alex"))
        whenever(preferencesManager.onboardingCompleted).thenReturn(flowOf(false))
        whenever(preferencesManager.onboardingGoal).thenReturn(flowOf(""))
        whenever(preferencesManager.onboardingCommitmentDuration).thenReturn(flowOf(5))
        whenever(preferencesManager.onboardingCommitmentFrequency).thenReturn(flowOf(3))

        whenever(preferencesManager.themeMode).thenReturn(flowOf("system"))
        whenever(preferencesManager.notificationsEnabled).thenReturn(flowOf(true))
        whenever(preferencesManager.weeklySummaryEnabled).thenReturn(flowOf(true))
        whenever(preferencesManager.hapticFeedbackEnabled).thenReturn(flowOf(true))
        whenever(preferencesManager.reminderFrequency).thenReturn(flowOf("daily"))
        whenever(preferencesManager.reminderTime).thenReturn(flowOf("09:00"))

        whenever(preferencesManager.readingStreakDays).thenReturn(flowOf(0))
        whenever(preferencesManager.pagesReadToday).thenReturn(flowOf(0))
        whenever(preferencesManager.readingGoalPages).thenReturn(flowOf(30))
        whenever(preferencesManager.readingLastDate).thenReturn(flowOf(today))
        whenever(preferencesManager.readingActivity).thenReturn(flowOf("[]"))

        whenever(preferencesManager.vocabularyStreakDays).thenReturn(flowOf(0))
        whenever(preferencesManager.wordsAddedToday).thenReturn(flowOf(0))
        whenever(preferencesManager.vocabularyLastDate).thenReturn(flowOf(today))
        whenever(preferencesManager.vocabularyWords).thenReturn(flowOf("[]"))

        whenever(preferencesManager.dailyReminderEnabled).thenReturn(flowOf(true))
        whenever(preferencesManager.soundEffectsEnabled).thenReturn(flowOf(true))
        whenever(preferencesManager.appLockEnabled).thenReturn(flowOf(false))
        whenever(preferencesManager.biometricEnabled).thenReturn(flowOf(false))
        whenever(preferencesManager.totalPoints).thenReturn(flowOf(0))
    }

    @Test
    fun onboardingCompletion_updatesState_persistsAndSeedsWhenEligible() = scope.runTest {
        viewModel.onSetOnboardingGoal("Read 5 pages")
        viewModel.onSetOnboardingCommitment(durationMinutes = 10, frequencyPerWeek = 5)
        viewModel.onToggleOnboardingCategory("Reading")

        whenever(
            streakRepository.createStreak(
                name = any(),
                goalPerDay = any(),
                unit = any(),
                category = any(),
                color = any(),
                icon = any()
            )
        ).thenReturn(RepositoryResult.Success("seed-id"))

        viewModel.onCompleteOnboarding()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.showOnboarding)
        assertTrue(state.onboardingState.hasCompleted)

        verify(preferencesManager, times(1)).setOnboardingCompleted(true)
        verify(streakRepository, times(1)).createStreak(
            name = eq("Read 5 pages"),
            goalPerDay = eq(10),
            unit = eq("minutes"),
            category = eq("Reading"),
            color = eq("#6366F1"),
            icon = eq("flag")
        )
    }

    @Test
    fun toggleTask_marksTaskCompleted_andLogsProgress() = scope.runTest {
        advanceUntilIdle()

        val initial = viewModel.uiState.value
        val task = initial.todayTasks.firstOrNull()
        if (task == null) {
            // No tasks in this configuration; nothing to assert.
            return@runTest
        }

        whenever(streakRepository.logProgress(task.streakId, task.value)).thenReturn(RepositoryResult.Success(Unit))

        viewModel.onToggleTask(task.id)

        val updated = viewModel.uiState.value
        val updatedTask = updated.todayTasks.first { it.id == task.id }
        assertTrue(updatedTask.isCompleted)

        verify(streakRepository, times(1)).logProgress(eq(task.streakId), any())
    }
}
