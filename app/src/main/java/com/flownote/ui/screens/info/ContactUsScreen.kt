package com.flownote.ui.screens.info

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.dimensionResource
import com.flownote.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Contact Us") },
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
                .padding(dimensionResource(id = R.dimen.spacing_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(id = R.dimen.top_app_bar_height)),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))
            
            Text(
                text = "Get in Touch",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_xsmall)))
            
            Text(
                text = "We'd love to hear from you! Send us your feedback or questions.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_xlarge)))
            
            Button(
                onClick = { /* TODO: Launch Email Intent */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Email")
            }
        }
    }
}
