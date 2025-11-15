package com.productivitystreak.ui.screens.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.state.discover.ChallengeItem
import com.productivitystreak.ui.state.discover.DiscoverState
import com.productivitystreak.ui.state.discover.FeaturedContent
import com.productivitystreak.ui.state.discover.SuggestionItem
import com.productivitystreak.ui.theme.Shapes
import com.productivitystreak.ui.theme.Spacing

@Composable
fun DiscoverScreen(state: DiscoverState) {
    val gradient = Brush.verticalGradient(listOf(Color(0xFFF1F4FF), Color(0xFFECE8FF)))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DiscoverHeader()
        InspirationCard(content = state.featuredContent)
        SuggestionsSection(items = state.suggestions)
        CommunitySection(challenges = state.communityChallenges)
    }
}

@Composable
private fun DiscoverHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "Never Zero", style = MaterialTheme.typography.labelLarge, color = Color(0xFF7176A3))
        Text(text = "Discover", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = "Stay inspired with new ideas.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7A7F96))
    }
}

@Composable
private fun InspirationCard(content: FeaturedContent) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.extraLarge,
        color = Color.White,
        tonalElevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Stay Inspired", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = content.description.ifBlank { "Micro-habits backed by behavioral science." },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A7F96)
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = Shapes.large,
                color = Color(0xFFE7E9FF)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = content.title.ifBlank { "Fresh ideas drop every morning" },
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF6A63FF)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Add new habits", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                    Text(text = "Curated ideas daily", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7F96))
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A63FF), contentColor = Color.White),
                    shape = Shapes.large
                ) {
                    Text(text = "Browse habit ideas")
                }
            }
        }
    }
}

@Composable
private fun SuggestionsSection(items: List<SuggestionItem>) {
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "New Habits to Try", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(text = "Pick one and log it via the + button below.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7F96))
        items.forEach { suggestion ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.large,
                color = Color.White,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = suggestion.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text(text = suggestion.subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7F96))
                    }
                    IconButton(onClick = { /* future hook */ }, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add habit", tint = Color(0xFF6860FF))
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunitySection(challenges: List<ChallengeItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Community", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CommunityCard(
                title = "Join a Group",
                subtitle = challenges.firstOrNull()?.title ?: "Find like-minded people",
                icon = Icons.Rounded.Group
            )
            CommunityCard(
                title = "Discussions",
                subtitle = challenges.getOrNull(1)?.title ?: "Share your progress",
                icon = Icons.Rounded.Chat
            )
        }
    }
}

@Composable
private fun RowScope.CommunityCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = Shapes.large,
        color = Color.White,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(shape = CircleShape, color = Color(0xFFEEF0FF)) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF6A63FF), modifier = Modifier.padding(10.dp))
            }
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7F96))
        }
    }
}
