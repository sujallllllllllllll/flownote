package com.flownote.ui.screens.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flownote.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Help & Guide",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Welcome Section
            WelcomeCard()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Getting Started
            SectionHeader("Getting Started")
            HelpItem(
                icon = Icons.Default.Add,
                title = "Create a Note",
                description = "Tap the + button at the bottom of the home screen to create a new note."
            )
            HelpItem(
                icon = Icons.Default.Edit,
                title = "Edit a Note",
                description = "Tap any note card to open and edit it. Changes are saved automatically."
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Features
            SectionHeader("Features")
            HelpItem(
                icon = Icons.Default.PushPin,
                title = "Pin Important Notes",
                description = "Tap the pin icon in the note editor to keep important notes at the top."
            )
            HelpItem(
                icon = Icons.Default.Search,
                title = "Search Notes",
                description = "Use the search bar to find notes by title, content, or tags."
            )
            HelpItem(
                icon = Icons.Default.Category,
                title = "Organize with Categories",
                description = "Assign categories like Tasks, Ideas, or Meetings to organize your notes."
            )
            HelpItem(
                icon = Icons.AutoMirrored.Filled.Label,
                title = "Add Tags",
                description = "Add tags to notes for better organization and quick filtering."
            )
            HelpItem(
                icon = Icons.Default.Notifications,
                title = "Set Reminders",
                description = "Tap the bell icon to set a reminder for any note."
            )
            HelpItem(
                icon = Icons.Default.Palette,
                title = "Customize Colors",
                description = "Choose from different colors to visually organize your notes."
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Gestures
            SectionHeader("Gestures & Actions")
            HelpItem(
                icon = Icons.Default.SwipeLeft,
                title = "Swipe to Delete",
                description = "Swipe a note card left to reveal the delete option."
            )
            HelpItem(
                icon = Icons.Default.TouchApp,
                title = "Long Press",
                description = "Long press on text to access formatting options."
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tips
            SectionHeader("Pro Tips")
            TipCard(
                "Auto-save is enabled! Your changes are saved automatically as you type."
            )
            TipCard(
                "Use the search debounce feature - the app waits 300ms after you stop typing to search."
            )
            TipCard(
                "All your notes are stored locally on your device. No internet required!"
            )
            TipCard(
                "The app supports both light and dark themes. Change it in Settings."
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Welcome to FlowNotes!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "A simple, offline-first note-taking app designed for privacy and ease of use.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun HelpItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TipCard(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
