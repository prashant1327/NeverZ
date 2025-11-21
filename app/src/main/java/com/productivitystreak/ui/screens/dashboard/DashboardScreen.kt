package com.productivitystreak.ui.screens.dashboard

// Dashboard UI removed during architectural sanitization.

import android.graphics.Color.parseColor
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.productivitystreak.data.model.Streak
import com.productivitystreak.ui.state.AppUiState
import com.productivitystreak.ui.state.DashboardTask
import com.productivitystreak.ui.state.vocabulary.VocabularyWord
import com.productivitystreak.ui.screens.vocabulary.extractPartOfSpeech
import com.productivitystreak.ui.theme.NeverZeroTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import java.time.LocalTime
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    uiState: AppUiState,
    onToggleTask: (String) -> Unit,
    onRefreshQuote: () -> Unit,
    onAddHabitClick: () -> Unit,
    onSelectStreak: (String) -> Unit,
    onAddOneOffTask: (String) -> Unit,
    onToggleOneOffTask: (String) -> Unit,
    onDeleteOneOffTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val greetingPrefix = remember {
        val hour = LocalTime.now().hour
        when {
            hour in 5..11 -> "Good Morning"
            hour in 12..16 -> "Good Afternoon"
            hour in 17..21 -> "Good Evening"
            else -> "Hello"
        }
    }

    val leadStreak = uiState.streaks.find { it.id == uiState.selectedStreakId }
        ?: uiState.streaks.firstOrNull()

    val progress by animateFloatAsState(
        targetValue = leadStreak?.progress ?: 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "lead-progress"
    )

    val confettiState = remember { mutableStateMapOf<String, Boolean>() }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title ->
                onAddOneOffTask(title)
                showAddTaskDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AnimatedContent(
                targetState = uiState.userName,
                label = "dashboard-greeting"
            ) { name ->
                Text(
                    text = "$greetingPrefix, $name! Let’s get to work.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        item {
            BuddhaMorningBriefCard(
                uiState = uiState,
                onRefreshQuote = onRefreshQuote,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (leadStreak != null) {
            item {
                LeadHabitCard(
                    streak = leadStreak,
                    progress = progress,
                    onClick = { onSelectStreak(leadStreak.id) }
                )
            }
        }

        item {
            DailyMicroLessonCard(
                word = uiState.vocabularyState.words.firstOrNull(),
                streakDays = uiState.vocabularyState.currentStreakDays,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = "Habits",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (uiState.todayTasks.isEmpty()) {
            item { DashboardEmptyState(onAddHabitClick = onAddHabitClick) }
        } else {
            items(uiState.todayTasks, key = { it.id }) { task ->
                val showConfetti = confettiState[task.id] == true

                DashboardTaskRow(
                    task = task,
                    onToggle = {
                        if (!task.isCompleted) {
                            if (uiState.profileState.hapticsEnabled) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            confettiState[task.id] = true
                            onToggleTask(task.id)
                        }
                    },
                    showConfetti = showConfetti
                )

                LaunchedEffect(showConfetti) {
                    if (showConfetti) {
                        delay(450)
                        confettiState[task.id] = false
                    }
                }
            }
        }

        // One-Off Tasks Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Add,
                        contentDescription = "Add Task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (uiState.oneOffTasks.isEmpty()) {
            item {
                Text(
                    text = "No tasks for today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        } else {
            items(uiState.oneOffTasks, key = { it.id }) { task ->
                OneOffTaskRow(
                    task = task,
                    onToggle = { onToggleOneOffTask(task.id) },
                    onDelete = { onDeleteOneOffTask(task.id) }
                )
            }
        }
    }
}

@Composable
private fun BuddhaMorningBriefCard(
    uiState: AppUiState,
    onRefreshQuote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val designColors = NeverZeroTheme.designColors
    val quote = uiState.quote
    val completed = uiState.todayTasks.count { it.isCompleted }
    val total = uiState.todayTasks.size
    val backgroundBrush = remember(designColors) {
        Brush.verticalGradient(
            listOf(
                designColors.surface.copy(alpha = 0.96f),
                designColors.backgroundAlt.copy(alpha = 0.94f)
            )
        )
    }

    val progressBarBrush = remember(designColors) {
        Brush.horizontalGradient(
            listOf(
                designColors.primary.copy(alpha = 0.9f),
                designColors.secondary.copy(alpha = 0.9f)
            )
        )
    }

    var tapped by remember { mutableStateOf(false) }
    val tapScale by animateFloatAsState(
        targetValue = if (tapped) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "buddha-tap-scale"
    )

    LaunchedEffect(tapped) {
        if (tapped) {
            delay(120)
            tapped = false
        }
    }

    Card(
        modifier = modifier
            .graphicsLayer(
                scaleX = tapScale,
                scaleY = tapScale
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = {
                tapped = true
                onRefreshQuote()
            }),
        colors = CardDefaults.cardColors(
            containerColor = designColors.surface
        ),
        border = BorderStroke(1.dp, designColors.border.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(brush = backgroundBrush)
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Morning brief",
                            style = MaterialTheme.typography.labelMedium,
                            color = designColors.textSecondary
                        )
                        Text(
                            text = "Keep the streak breathing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = designColors.textSecondary
                        )
                    }

                    if (uiState.isQuoteLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = designColors.primary
                        )
                    } else {
                        Text(
                            text = "Refresh",
                            style = MaterialTheme.typography.labelMedium,
                            color = designColors.primary
                        )
                    }
                }

                quote?.let {
                    Text(
                        text = "\"${it.text}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = designColors.textPrimary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = it.author,
                        style = MaterialTheme.typography.labelSmall,
                        color = designColors.textSecondary
                    )
                } ?: run {
                    Text(
                        text = "Today is a clean slate. Take one decisive action.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = designColors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (total > 0) {
                    val progressText = "$completed / $total habits logged"
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.labelSmall,
                        color = designColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val fraction = (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(designColors.border.copy(alpha = 0.5f))
                    ) {
                        if (fraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .height(4.dp)
                                    .background(progressBarBrush)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No habits scheduled. Set one tiny target.",
                        style = MaterialTheme.typography.labelSmall,
                        color = designColors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun LeadHabitCard(
    streak: Streak,
    progress: Float,
    onClick: () -> Unit
) {
    val designColors = NeverZeroTheme.designColors
    val gradient = remember(designColors) {
        Brush.linearGradient(
            colors = listOf(
                designColors.primary.copy(alpha = 0.34f),
                designColors.secondary.copy(alpha = 0.40f)
            )
        )
    }

    val highlightBrush = remember {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.18f),
                Color.Transparent
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, designColors.border.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = gradient
                )
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(highlightBrush)
            )

            Column(
                modifier = Modifier.align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Lead habit",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Text(
                    text = streak.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Goal • ${streak.goalPerDay} ${streak.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "${streak.currentCount} day streak",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.82f)
                )
            }

            CircularProgressRing(
                modifier = Modifier.align(Alignment.CenterEnd),
                progress = progress.coerceIn(0f, 1f)
            )
        }
    }
}

@Composable
private fun DailyMicroLessonCard(
    word: VocabularyWord?,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    if (word == null) return

    val designColors = NeverZeroTheme.designColors
    var offsetX by remember(word) { mutableStateOf(0f) }
    var mastered by remember(word) { mutableStateOf(false) }
    val density = LocalDensity.current
    val swipeThresholdPx = with(density) { 96.dp.toPx() }

    val cardBackgroundBrush = remember(designColors) {
        Brush.verticalGradient(
            listOf(
                designColors.surface.copy(alpha = 0.98f),
                designColors.backgroundAlt.copy(alpha = 0.96f)
            )
        )
    }

    val cardBorderColor = remember(designColors) {
        designColors.border.copy(alpha = 0.9f)
    }

    val tiltMaxDegrees = 4f
    val normalizedOffset = if (swipeThresholdPx == 0f) 0f else (offsetX / swipeThresholdPx).coerceIn(0f, 1.2f)
    val cardTilt = normalizedOffset * tiltMaxDegrees

    val masteredScale = remember { Animatable(1f) }

    LaunchedEffect(mastered) {
        if (mastered) {
            masteredScale.snapTo(1f)
            masteredScale.animateTo(
                targetValue = 0.97f,
                animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing)
            )
            masteredScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            masteredScale.snapTo(1f)
        }
    }

    Box(
        modifier = modifier
            .pointerInput(word) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > swipeThresholdPx) {
                            mastered = true
                        }
                        offsetX = 0f
                    }
                ) { _, dragAmount ->
                    offsetX = (offsetX + dragAmount).coerceIn(0f, swipeThresholdPx * 1.5f)
                }
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = offsetX
                    rotationZ = cardTilt
                    scaleX = masteredScale.value
                    scaleY = masteredScale.value
                },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, cardBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(cardBackgroundBrush)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Daily micro-lesson",
                                style = MaterialTheme.typography.labelMedium,
                                color = designColors.textSecondary
                            )
                            Text(
                                text = "Swipe to master",
                                style = MaterialTheme.typography.bodySmall,
                                color = designColors.textSecondary
                            )
                        }

                        Text(
                            text = "${streakDays}d streak",
                            style = MaterialTheme.typography.labelMedium,
                            color = designColors.primary
                        )
                    }

                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.headlineSmall,
                        color = designColors.textPrimary
                    )

                    Text(
                        text = extractPartOfSpeech(word.definition) ?: "learning word",
                        style = MaterialTheme.typography.bodySmall,
                        color = designColors.textSecondary
                    )

                    Text(
                        text = word.definition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = designColors.textSecondary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (mastered) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(designColors.success.copy(alpha = 0.14f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Circuit closed • mastered",
                                style = MaterialTheme.typography.labelSmall,
                                color = designColors.success
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CircularProgressRing(
    modifier: Modifier = Modifier,
    progress: Float
) {
    val ringSize = 80.dp
    val strokeWidth = 10.dp

    Canvas(modifier = modifier.size(ringSize)) {
        val sweep = 360f * progress
        val stroke = strokeWidth.toPx()
        val radius = size.minDimension / 2f - stroke

        drawCircle(
            color = Color.White.copy(alpha = 0.25f),
            radius = radius,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )

        drawArc(
            color = Color.White,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = stroke,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}

@Composable
private fun DashboardTaskRow(
    task: DashboardTask,
    onToggle: () -> Unit,
    showConfetti: Boolean
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val accent = hexToColor(task.accentHex, primaryColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = !task.isCompleted, onClick = onToggle),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = accent
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(accent)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = task.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = if (task.isCompleted) "Done" else "Tap to log",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (task.isCompleted) NeverZeroTheme.semanticColors.Success else MaterialTheme.colorScheme.primary
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = showConfetti,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.matchParentSize()
            ) {
                ConfettiOverlay(color = accent)
            }
        }
    }
}

@Composable
private fun ConfettiOverlay(color: Color) {
    val designColors = NeverZeroTheme.designColors
    val progress = remember { Animatable(0f) }

    val circuitBrush = remember(color, designColors) {
        Brush.horizontalGradient(
            listOf(color, designColors.primary, designColors.secondary)
        )
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val p = progress.value
        val centerY = size.height * 0.5f
        val startX = size.width * 0.1f
        val endX = size.width * 0.9f
        val currentX = startX + (endX - startX) * p
        val stroke = 3.dp.toPx()

        drawLine(
            color = designColors.border.copy(alpha = 0.7f),
            start = Offset(startX, centerY),
            end = Offset(endX, centerY),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            brush = circuitBrush,
            start = Offset(startX, centerY),
            end = Offset(currentX, centerY),
            strokeWidth = stroke * 1.5f,
            cap = StrokeCap.Round
        )

        val rippleRadius = 18.dp.toPx()
        val origin = Offset(startX, centerY)
        drawCircle(
            color = color.copy(alpha = (1f - p) * 0.8f),
            radius = rippleRadius * p,
            center = origin
        )
        drawCircle(
            color = designColors.glow.copy(alpha = (1f - p) * 0.6f),
            radius = rippleRadius * (0.4f + 0.6f * p),
            center = origin
        )
    }
}

@Composable
private fun DashboardEmptyState(onAddHabitClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty-state-breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing-scale"
    )
    
    val designColors = NeverZeroTheme.designColors
    val cardBackgroundBrush = remember(designColors) {
        Brush.verticalGradient(
            listOf(
                designColors.surface.copy(alpha = 0.98f),
                designColors.backgroundAlt.copy(alpha = 0.96f)
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = designColors.textPrimary
        ),
        border = BorderStroke(1.dp, designColors.border.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackgroundBrush)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f))
                    .graphicsLayer {
                        scaleX = breathingScale
                        scaleY = breathingScale
                    },
                contentAlignment = Alignment.Center
            ) {
                val primaryAlpha = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                val oceanStart = NeverZeroTheme.gradientColors.OceanStart.copy(alpha = 0.9f)
                val oceanEnd = NeverZeroTheme.gradientColors.OceanEnd.copy(alpha = 0.9f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val minDim = kotlin.math.min(size.width, size.height)

                    drawCircle(
                        color = primaryAlpha,
                        radius = minDim / 3
                    )
                    drawCircle(
                        color = oceanStart,
                        radius = minDim / 6,
                        center = center
                    )
                    drawCircle(
                        color = oceanEnd,
                        radius = minDim / 9,
                        center = center + Offset(minDim / 9, -minDim / 9)
                    )
                }
            }

            Text(
                text = "No habits for today",
                style = MaterialTheme.typography.titleLarge,
                color = designColors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Enjoy your free time or add a new habit to keep the streak alive.",
                style = MaterialTheme.typography.bodyMedium,
                color = designColors.textSecondary,
                modifier = Modifier.padding(horizontal = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            com.productivitystreak.ui.components.PrimaryButton(
                text = "Add a habit",
                onClick = onAddHabitClick,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun SoftPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Press -> pressed = true
                is androidx.compose.foundation.interaction.PressInteraction.Release,
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> pressed = false
            }
        }
    }

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "button-scale"
    )

    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale),
        interactionSource = interactionSource,
        shape = RoundedCornerShape(999.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp
        )
    }
}

private fun hexToColor(hex: String, fallback: Color): Color {
    return try {
        Color(parseColor(hex))
    } catch (_: IllegalArgumentException) {
        fallback
    }
}

@Composable
private fun OneOffTaskRow(
    task: com.productivitystreak.data.model.Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onToggle),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (task.isCompleted) NeverZeroTheme.semanticColors.Success else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Task") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("What needs to be done?") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
