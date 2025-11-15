package com.productivitystreak.data.repository

import com.productivitystreak.data.local.dao.StreakDao
import com.productivitystreak.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class StreakRepositoryTest {

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var streakDao: StreakDao

    private lateinit var streakRepository: StreakRepository

    @Before
    fun setUp() {
        streakRepository = StreakRepository(streakDao)
    }

    @Test
    fun `observeStreaks should map entities to streaks`() = runBlocking {
        // Given
        val streakEntities = listOf(
            StreakEntity(
                id = "test-id",
                name = "Test Streak",
                currentCount = 5,
                longestCount = 10,
                goalPerDay = 1,
                unit = "times",
                category = "test",
                history = emptyList(),
                color = "#FF0000",
                icon = "test"
            )
        )
        whenever(streakDao.getAllStreaks()).thenReturn(flowOf(streakEntities))

        // When
        val result = streakRepository.observeStreaks().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("test-id", result[0].id)
        assertEquals("Test Streak", result[0].name)
        assertEquals(5, result[0].currentCount)
    }

    @Test
    fun `createStreak should insert new streak entity`() = runBlocking {
        // Given
        val name = "New Streak"
        val goalPerDay = 2
        val unit = "pages"
        val category = "Reading"
        val color = "#00FF00"
        val icon = "book"

        // When
        val result = streakRepository.createStreak(name, goalPerDay, unit, category, color, icon)

        // Then
        assertTrue(result is RepositoryResult.Success)
        org.mockito.Mockito.verify(streakDao).insertStreak(any())
    }
}