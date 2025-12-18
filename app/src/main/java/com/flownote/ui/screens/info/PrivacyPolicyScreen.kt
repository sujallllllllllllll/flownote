package com.flownote.ui.screens.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.dimensionResource
import com.flownote.R

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.screen_margin_horizontal))
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
            
            Text(
                text = "Last updated: December 2025",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

            SectionTitle("1. Data Collection")
            BodyText("FlowNotes is an offline-first application. We do not collect, store, or transmit your personal data to any external servers. All your notes and recordings are stored locally on your device.")

            SectionTitle("2. Audio Permissions")
            BodyText("The app requires microphone access solely for the purpose of recording voice notes. These recordings are stored locally and are never shared.")

            SectionTitle("3. Storage")
            BodyText("We use local database storage to save your notes. You are in full control of your data.")

            SectionTitle("4. Contact")
            BodyText("If you have any questions about this privacy policy, please contact us via the Contact Us page.")
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_xsmall))
    )
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_medium))
    )
}

@Composable
private fun Spacer(modifier: Modifier) {
    androidx.compose.foundation.layout.Spacer(modifier = modifier)
}
