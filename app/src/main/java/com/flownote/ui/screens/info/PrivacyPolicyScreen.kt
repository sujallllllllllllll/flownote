package com.flownote.ui.screens.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flownote.R

/**
 * Privacy Policy screen - Clear, human-readable privacy explanation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Privacy Policy") },
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
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_medium)) // 16dp - tighter flow
        ) {

            
            // Privacy Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_medium)),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_medium))
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Column {
                        Text(
                            text = "FlowNotes respects your privacy.\nYour notes stay on your device.\nWe do not track, collect, or sell your data.",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // Last Updated
            Text(
                text = "Last updated: December 2025",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Privacy Sections
            PrivacySection(
                title = "Data Collection",
                content = "No personal data collected, nothing sent to external servers."
            )
            
            PrivacySection(
                title = "Audio & Permissions",
                content = "Microphone used only for voice features. Audio stored locally on your device."
            )
            
            PrivacySection(
                title = "Storage",
                content = "Notes are saved locally on your device. Uninstalling the app removes all local data."
            )
            
            PrivacySection(
                title = "Internet Usage",
                content = "App works fully offline. No background data sync, no tracking, no analytics."
            )
            
            PrivacySection(
                title = "Contact",
                content = "Privacy questions can be asked via the Contact Us page."
            )
            

        }
    }
}

@Composable
private fun PrivacySection(
    title: String,
    content: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
