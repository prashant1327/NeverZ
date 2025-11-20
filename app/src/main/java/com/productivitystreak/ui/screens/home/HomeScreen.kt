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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
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
    onOpenNotificationsSettings: () -> Unit = {},
    onOpenVocabulary: () -> Unit = {}
) {
    val wordOfTheDay by viewModel.wordOfTheDay.collectAsStateWithLifecycle()
    val greeting = remember { getGreeting() }
    val streakDays = uiState.vocabularyState.currentStreakDays

    LaunchedEffect(uiState.vocabularyState.words) {
        viewModel.refreshWordForToday(uiState.vocabularyState.words)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$greeting, ${uiState.userName}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Vocabulary teacher",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val streakColor = Color(0xFFFF5722)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalFireDepartment,
                    contentDescription = "Vocabulary streak",
                    tint = streakColor
                )
                Text(
                    text = streakDays.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = streakColor
                )
            }
        }

        WordOfTheDayCard(
            word = wordOfTheDay,
            onPrimaryAction = {
                viewModel.startLearningWord()
                onOpenVocabulary()
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Today’s habits",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.todayTasks, key = { it.id }) { task ->
                HabitItemRow(
                    task = task,
                    onToggle = { onHabitToggle(task.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun WordOfTheDayCard(
    word: VocabularyWord?,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayWord = word?.word ?: "Stoicism"
    val displayPronunciation = word?.let { "/${it.word.lowercase()}/" } ?: "/ˈstōəˌsizəm/"
    val displayDefinition = word?.definition
        ?: "The endurance of hardship without complaint; a calm acceptance of life’s ups and downs."

    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "WORD OF THE DAY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = displayWord,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = displayPronunciation,
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = displayDefinition,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onPrimaryAction,
                enabled = word != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add to Vocabulary")
            }
        }
    }
}

@Composable
private fun HabitItemRow(
    task: DashboardTask,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tapped by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (tapped) 0.94f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "habit-bounce"
    )
    val haptics = LocalHapticFeedback.current

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
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
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
/**
 * Returns a friendly greeting based on the current local time.
 */
fun getGreeting(now: LocalTime = LocalTime.now()): String {
    val hour = now.hour
    return when (hour) {
        in 5..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
