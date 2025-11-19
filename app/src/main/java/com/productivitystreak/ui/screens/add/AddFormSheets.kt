package com.productivitystreak.ui.screens.add

// Add form sheets removed during architectural sanitization.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.ui.state.AddEntryType

@Composable
fun AddEntryMenuSheet(
    onEntrySelected: (AddEntryType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What would you like to add?",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        AddMenuCard(
            title = "New habit",
            subtitle = "Track a new routine with reminders.",
            onClick = { onEntrySelected(AddEntryType.HABIT) }
        )
        AddMenuCard(
            title = "Log words",
            subtitle = "Capture vocabulary for 'Add 5 Words'.",
            onClick = { onEntrySelected(AddEntryType.WORD) }
        )
        AddMenuCard(
            title = "Journal",
            subtitle = "Reflect on your day and mood.",
            onClick = { onEntrySelected(AddEntryType.JOURNAL) }
        )
    }
}

@Composable
private fun AddMenuCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = Modifier
            .fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFormSheet(
    isSubmitting: Boolean,
    onSubmit: (name: String, goal: Int, unit: String, category: String, color: String?, icon: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var goalText by remember { mutableStateOf("10") }
    var unit by remember { mutableStateOf("minutes") }
    var category by remember { mutableStateOf("Focus") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Create a lead habit",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Give it a clear name and a realistic daily target.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Habit name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = goalText,
                onValueChange = { value ->
                    if (value.length <= 3 && value.all { it.isDigit() }) {
                        goalText = value
                    }
                },
                label = { Text("Goal") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        val goal = goalText.toIntOrNull()?.coerceAtLeast(1) ?: 1
        Button(
            onClick = { onSubmit(name.trim(), goal, unit.trim(), category.trim(), null, null) },
            enabled = !isSubmitting && name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = if (isSubmitting) "Saving…" else "Save habit")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyFormSheet(
    isSubmitting: Boolean,
    onSubmit: (word: String, definition: String, example: String?) -> Unit
) {
    var word by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Log vocabulary",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Capture the word and its meaning so it sticks.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = word,
            onValueChange = { word = it },
            label = { Text("Word") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = definition,
            onValueChange = { definition = it },
            label = { Text("Definition") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = example,
            onValueChange = { example = it },
            label = { Text("Example sentence (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onSubmit(word.trim(), definition.trim(), example.trim().ifBlank { null }) },
            enabled = !isSubmitting && word.isNotBlank() && definition.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text(text = if (isSubmitting) "Saving…" else "Save word")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalFormSheet(
    isSubmitting: Boolean,
    onSubmit: (mood: Int, notes: String, highlights: String?, gratitude: String?, tomorrowGoals: String?) -> Unit
) {
    var mood by remember { mutableStateOf(3f) }
    var notes by remember { mutableStateOf("") }
    var highlights by remember { mutableStateOf("") }
    var gratitude by remember { mutableStateOf("") }
    var tomorrowGoals by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Evening reflection",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "A quick check-in to keep your streak intentional.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = moodEmoji(mood),
                fontSize = 30.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Mood", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = mood,
                    onValueChange = { mood = it },
                    valueRange = 1f..5f,
                    steps = 3
                )
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("What stood out today?") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        OutlinedTextField(
            value = highlights,
            onValueChange = { highlights = it },
            label = { Text("Highlights (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        OutlinedTextField(
            value = gratitude,
            onValueChange = { gratitude = it },
            label = { Text("Gratitude (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        OutlinedTextField(
            value = tomorrowGoals,
            onValueChange = { tomorrowGoals = it },
            label = { Text("Tomorrow's focus (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Button(
            onClick = {
                onSubmit(
                    mood.toInt(),
                    notes.trim(),
                    highlights.trim().ifBlank { null },
                    gratitude.trim().ifBlank { null },
                    tomorrowGoals.trim().ifBlank { null }
                )
            },
            enabled = !isSubmitting && notes.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text(text = if (isSubmitting) "Saving…" else "Save entry")
        }
    }
}

private fun moodEmoji(value: Float): String = when (value.toInt()) {
    1 -> "🙁"
    2 -> "😐"
    3 -> "🙂"
    4 -> "😀"
    else -> "🤩"
}
