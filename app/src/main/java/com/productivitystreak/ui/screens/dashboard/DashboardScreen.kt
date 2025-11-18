package com.productivitystreak.ui.screens.dashboard

// Dashboard UI removed during architectural sanitization.
    onReviewProgress: () -> Unit,
    goalHabit: String,
    commitmentMinutes: Int,
    commitmentFrequency: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Getting started", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            val highlight = if (goalHabit.isNotBlank()) goalHabit else "your first habit"
            Text(text = "Complete these steps to launch $highlight.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
            ChecklistRow(
                title = if (goalHabit.isNotBlank()) "Add \"$goalHabit\"" else "Add your first habit",
                description = "Pick one from Discover or create your own.",
                completed = false,
                onClick = onAddHabit
            )
            ChecklistRow(
                title = "Enable reminders",
                description = "Stay on track with gentle nudges.",
                completed = notificationsEnabled,
                onClick = onEnableNotifications
            )
            ChecklistRow(
                title = "Log today's progress",
                description = "${commitmentMinutes} mins · ${commitmentFrequency}x/week",
                completed = hasLoggedToday,
                onClick = onReviewProgress
            )
        }
    }
}

@Composable
private fun ChecklistRow(
    title: String,
    description: String,
    completed: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.large)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(shape = CircleShape, color = if (completed) Color(0xFFE4F8E8) else Color(0xFFF0F1FF)) {
            Icon(
                imageVector = if (completed) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (completed) Color(0xFF2E7D32) else Color(0xFF5F6BFF),
                modifier = Modifier.padding(10.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
        }
        Text(text = if (completed) "Done" else "Start", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun NotificationNudgeCard(onEnableNotifications: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color(0xFFFFF6E8),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Surface(shape = CircleShape, color = Color.White) {
                    Icon(imageVector = Icons.Rounded.Notifications, contentDescription = null, tint = Color(0xFFEF6C00), modifier = Modifier.padding(12.dp))
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Never miss a check-in", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(text = "Enable reminders so we can nudge you before the day ends.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF935F1B))
                }
            }
            TextButton(onClick = onEnableNotifications) {
                Text(text = "Enable")
            }
        }
    }
}

@Composable
private fun HeroStreakCard(
    streak: Streak?,
    totalStreaks: Int,
    onRefreshQuote: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Lead habit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "$totalStreaks active streaks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onRefreshQuote) {
                    Text(text = stringResource(id = R.string.dashboard_action_get_motivated))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = streak?.name ?: "No habit selected", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = when {
                            streak == null -> "Create your first streak"
                            streak.currentCount == 0 -> "Log progress to start"
                            else -> "${streak.currentCount} day streak"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (streak != null) {
                val progressPercent = (streak.progress * 100).roundToInt().coerceIn(0, 100)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Today's Progress", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LinearProgressIndicator(
                        progress = { streak.progress.coerceIn(0f, 1f) },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(text = "$progressPercent% complete", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                }
            } else {
                Text(text = "Add a habit to unlock insights", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onNavigateToReading: () -> Unit,
    onNavigateToVocabulary: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToDiscover: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionCard(
            title = "Reading Log",
            subtitle = "Track chapters",
            icon = Icons.AutoMirrored.Rounded.MenuBook,
            onClick = onNavigateToReading,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "New Word",
            subtitle = "Expand vocab",
            icon = Icons.Rounded.Add,
            onClick = onNavigateToVocabulary,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "Discover",
            subtitle = "Browse habit ideas",
            icon = Icons.Rounded.CompassCalibration,
            onClick = onNavigateToDiscover,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = Shapes.large,
        color = Color.White,
        tonalElevation = 6.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(shape = CircleShape, color = Color(0xFFEDF0FF)) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF6757FF), modifier = Modifier.padding(10.dp))
            }
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
        }
    }
}

@Composable
private fun HabitCalloutCard(onNavigateToDiscover: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                Text(text = "Need a fresh habit?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "Tap the + button or explore curated ideas.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
            }
            TextButton(onClick = onNavigateToDiscover) {
                Text("Explore")
            }
        }
    }
}

@Composable
private fun StreakGridSection(
    streaks: List<Streak>,
    selectedId: String?,
    onSelectStreak: (String) -> Unit,
    onAddHabit: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Your Streaks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

        if (streaks.isEmpty()) {
            EmptyStateCard(message = "Add a habit to begin logging streaks.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                streaks.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { streak ->
                            StreakCard(
                                streak = streak,
                                selected = streak.id == selectedId,
                                modifier = Modifier.weight(1f)
                            ) { onSelectStreak(streak.id) }
                        }
                        if (row.size == 1) {
                            AddHabitCard(modifier = Modifier.weight(1f), onClick = onAddHabit)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakCard(
    streak: Streak,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accent = getCategoryColors(streak.category)
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "streak-scale"
    )

    Surface(
        modifier = modifier
            .height(170.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        shape = Shapes.large,
        color = Color.White,
        tonalElevation = if (selected) 10.dp else 4.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = streak.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = streak.category, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { streak.progress.coerceIn(0f, 1f) },
                color = accent.first,
                trackColor = accent.second.copy(alpha = 0.4f)
            )
            Text(text = "${streak.currentCount} days", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "Goal: ${streak.goalPerDay} ${streak.unit}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
        }
    }
}

@Composable
private fun AddHabitCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(170.dp)
            .clip(Shapes.large)
            .border(BorderStroke(Border.thin, Color(0xFFCBD3FF)), Shapes.large)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = CircleShape, color = Color(0xFFECEFFF)) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color(0xFF5F6BFF), modifier = Modifier.padding(12.dp))
            }
            Text(text = "New Habit", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
            Text(text = "Launch Discover to begin", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
        }
    }
}

@Composable
private fun PrimaryActionButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6B63FF),
            disabledContainerColor = Color(0xFFCBCFEF),
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        )
    ) {
        Box(modifier = Modifier.padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
            Text(text = label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DailyInspirationPanel(
    quote: String,
    author: String?,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Daily Inspiration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onRefresh, enabled = !isLoading) {
                    Text("Refresh")
                }
            }

            AnimatedContent(targetState = isLoading, label = "quote-state") { loading ->
                if (loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "\"$quote\"", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        if (!author.isNullOrBlank()) {
                            Text(text = author, style = MaterialTheme.typography.labelLarge, color = Color(0xFF7C819C))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskListSection(
    tasks: List<DashboardTask>,
    completedTasks: Int,
    onToggleTask: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Today's Focus", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "$completedTasks/${tasks.size} completed", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
                }
                TextButton(onClick = { /* future expand */ }) {
                    Text("View All")
                }
            }

            if (tasks.isEmpty()) {
                EmptyStateCard(message = "No tasks scheduled. Add a habit to begin.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tasks.forEach { task ->
                        TaskRow(task = task, onToggleTask = onToggleTask)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: DashboardTask, onToggleTask: (String) -> Unit) {
    val categoryColors = getCategoryColors(task.category)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.large,
        color = Color(0xFFF6F7FF)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) categoryColors.first else categoryColors.second),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(targetState = task.isCompleted, label = "task-icon") { completed ->
                    Icon(
                        imageVector = if (completed) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (completed) Color.White else categoryColors.first
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(text = task.category, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
            }
            TextButton(onClick = { if (!task.isCompleted) onToggleTask(task.id) }, enabled = !task.isCompleted) {
                Text(if (task.isCompleted) "Done" else "Log")
            }
        }
    }
}

@Composable
private fun CommunityTeaser(onNavigateToDiscover: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 6.dp,
        onClick = onNavigateToDiscover
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Community", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "Join groups and discussions", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C819C))
            }
            Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color(0xFF7B61FF))
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.large,
        color = Color(0xFFF6F7FF)
    ) {
        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.CenterStart) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7C819C))
        }
    }
}

private fun getCategoryColors(category: String): Triple<Color, Color, Color> {
    return when (category.lowercase()) {
        "fitness" -> Triple(Color(0xFF6B63FF), Color(0xFFECEBFF), Color(0xFF352C79))
        "health" -> Triple(Color(0xFF3DD598), Color(0xFFDFF9ED), Color(0xFF0B5C3A))
        "mindfulness" -> Triple(Color(0xFF6FD6FF), Color(0xFFE3F6FF), Color(0xFF0B3D52))
        "learning" -> Triple(Color(0xFFFFC542), Color(0xFFFFF3D6), Color(0xFF4A2E00))
        "career" -> Triple(Color(0xFFFF8A65), Color(0xFFFFE5DC), Color(0xFF5F230B))
        else -> Triple(Color(0xFF7B61FF), Color(0xFFEAE5FF), Color(0xFF2E1F66))
    }
}
