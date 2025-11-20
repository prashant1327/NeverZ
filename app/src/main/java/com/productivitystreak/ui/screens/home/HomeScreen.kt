package com.productivitystreak.ui.screens.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.icons.AppIcons
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.state.profile.ReminderFrequency
import com.productivitystreak.ui.state.vocabulary.VocabularyWord
import com.productivitystreak.ui.theme.NeverZeroTheme
import java.time.LocalTime
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    uiState: AppUiState,
    viewModel: HomeViewModel,
    onHabitToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    onOpenNotificationsSettings: () -> Unit = {}
) {
    val wordOfTheDay by viewModel.wordOfTheDay.collectAsStateWithLifecycle()
    val greeting = remember { getGreeting() }

    val remainingHabits = remember(uiState.todayTasks) {
        uiState.todayTasks.count { !it.isCompleted }
    }

    val leadStreak = remember(uiState.streaks, uiState.selectedStreakId) {
        uiState.streaks.find { it.id == uiState.selectedStreakId } ?: uiState.streaks.firstOrNull()
    }

    val leadProgress by animateFloatAsState(
        targetValue = leadStreak?.progress ?: 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "home-lead-progress"
    )

    val achievementText = remember(uiState) {
        val vocabToday = uiState.vocabularyState.wordsAddedToday
        val totalHabits = uiState.todayTasks.size
        val doneHabits = uiState.todayTasks.count { it.isCompleted }
        val reading = uiState.readingTrackerState

        when {
            vocabToday >= 3 -> "You’ve logged $vocabToday words today. Nice streak."
            totalHabits > 0 && doneHabits == totalHabits -> "All habits for today are done. Enjoy your free time."
            reading.progressFraction >= 1f -> "Reading goal complete: ${reading.pagesReadToday}/${reading.goalPagesPerDay} pages."
            else -> null
        }
    }

    val focusLabel = remember(uiState.profileState.activeCategories) {
        uiState.profileState.activeCategories.joinToString().ifBlank { null }
    }

    LaunchedEffect(uiState.vocabularyState.words) {
        viewModel.refreshWordForToday(uiState.vocabularyState.words)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (remainingHabits > 0) {
                "$greeting, ${uiState.userName} — $remainingHabits tiny wins left today."
            } else {
                "$greeting, ${uiState.userName} — you’ve hit all your habits."
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        focusLabel?.let { label ->
            Text(
                text = "Focus: $label",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        achievementText?.let { message ->
            AchievementCard(text = message)
        }

        WordOfTheDayCard(
            word = wordOfTheDay,
            onKnow = {
                viewModel.markWordKnown()
            },
            onLearn = {
                viewModel.startLearningWord()
            },
            modifier = Modifier.fillMaxWidth()
        )

        leadStreak?.let { streak ->
            LeadHabitSummaryCard(
                streak = streak,
                progress = leadProgress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ReadingProgressCard(
            pagesReadToday = uiState.readingTrackerState.pagesReadToday,
            goalPagesPerDay = uiState.readingTrackerState.goalPagesPerDay,
            progressFraction = uiState.readingTrackerState.progressFraction,
            modifier = Modifier.fillMaxWidth()
        )

        val notificationsDisabled =
            !uiState.profileState.notificationEnabled || uiState.profileState.reminderFrequency == ReminderFrequency.None

        if (notificationsDisabled) {
            NotificationNudgeCard(onOpenSettings = onOpenNotificationsSettings)
        }

        Text(
            text = "Today’s habits",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val nextTaskId = uiState.todayTasks.firstOrNull { !it.isCompleted }?.id
            items(uiState.todayTasks, key = { it.id }) { task ->
                HabitItemRow(
                    task = task,
                    onToggle = { onHabitToggle(task.id) },
                    isNext = task.id == nextTaskId
                )
            }
        }
    }
}

@Composable
fun WordOfTheDayCard(
    word: VocabularyWord?,
    onKnow: () -> Unit,
    onLearn: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Word of the day",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = word?.word ?: "No word selected yet",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            word?.definition?.let { definition ->
                Text(
                    text = definition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onKnow,
                    enabled = word != null
                ) {
                    Text("I know this")
                }
                Button(
                    onClick = onLearn,
                    enabled = word != null
                ) {
                    Text("Learn")
                }
            }
        }
    }
}

@Composable
private fun HabitItemRow(
    task: DashboardTask,
    onToggle: () -> Unit,
    isNext: Boolean,
    modifier: Modifier = Modifier
) {
    var tapped by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (tapped) 0.94f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "habit-bounce"
    )

    LaunchedEffect(tapped) {
        if (tapped) {
            // Brief bounce before marking the habit as done.
            delay(110)
            onToggle()
            tapped = false
        }
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                if (!tapped) {
                    tapped = true
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isNext && !task.isCompleted) {
                Text(
                    text = "Next up",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = task.category,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LeadHabitSummaryCard(
    streak: Streak,
    progress: Float,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Lead habit",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = streak.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Goal — ${streak.goalPerDay} ${streak.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            CircularProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                strokeWidth = 6.dp,
                modifier = Modifier.height(40.dp)
            )
        }
    }
}

@Composable
private fun ReadingProgressCard(
    pagesReadToday: Int,
    goalPagesPerDay: Int,
    progressFraction: Float,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Reading",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$pagesReadToday / $goalPagesPerDay pages today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            LinearProgressIndicator(
                progress = progressFraction.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AchievementCard(text: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = AppIcons.Celebration,
                contentDescription = null,
                tint = NeverZeroTheme.semanticColors.Success
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun NotificationNudgeCard(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Want a gentle evening nudge?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedButton(onClick = onOpenSettings) {
                Text("Enable reminders")
            }
        }
    }
}

/**
 * Returns a friendly greeting based on the current local time.
 */
fun getGreeting(now: LocalTime = LocalTime.now()): String {
    val hour = now.hour
    return if (hour in 5..16) "Good Morning" else "Good Evening"
}
