package com.flownote.ui.screens.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flownote.R

/**
 * Upcoming Features screen - Transparent preview of future additions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingFeaturesScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Upcoming Features") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(id = R.dimen.screen_margin_horizontal)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_large))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
            
            // Intro Icon
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )
            
            // Intro Text
            Text(
                text = "Here are a few features we're exploring for future versions of FlowNotes.\nEverything is optional and added gradually.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_small)))
            
            // Features List - Top 5 Upcoming
            FeatureItem(
                title = "Note Templates",
                description = "Pre-built formats for meetings, tasks, recipes, and journaling"
            )
            
            FeatureItem(
                title = "Local Backup & Restore",
                description = "Export and import your notes as encrypted ZIP files"
            )
            
            FeatureItem(
                title = "Auto-Delete Temporary Notes",
                description = "Set notes to automatically delete after a specified time"
            )
            
            FeatureItem(
                title = "Voice Notes & Recording",
                description = "Record audio directly in notes without internet"
            )
            
            FeatureItem(
                title = "Home Screen Widgets",
                description = "Quick note access and creation from your home screen"
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_small)))
            
            // Disclaimer Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_medium)),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Features may change or evolve over time.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "FlowNotes will always remain offline-first and privacy-focused.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Feedback Loop
            Text(
                text = "Want to influence what ships next?\nShare your thoughts from the Contact Us page.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
        }
    }
}

@Composable
private fun FeatureItem(
    title: String,
    description: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "• $title",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
