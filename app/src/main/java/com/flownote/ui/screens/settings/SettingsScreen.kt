package com.flownote.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.flownote.R
import com.flownote.data.repository.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val useDynamicColors by viewModel.useDynamicColors.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Appearance Section
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.screen_margin_horizontal), 
                    top = dimensionResource(id = R.dimen.spacing_medium), 
                    bottom = dimensionResource(id = R.dimen.spacing_xsmall)
                )
            )

            // Theme Selection
            ListItem(
                headlineContent = { Text("App Theme") },
                supportingContent = {
                    Text(
                        when (themeMode) {
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.DARK -> "Dark"
                            ThemeMode.SYSTEM -> "System Default"
                        }
                    )
                },
                modifier = Modifier.clickable { showThemeDialog = true }
            )

            // Dynamic Color Toggle (Only Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ListItem(
                    headlineContent = { Text("Dynamic Colors") },
                    supportingContent = { Text("Use wallpaper colors") },
                    trailingContent = {
                        Switch(
                            checked = useDynamicColors,
                            onCheckedChange = { viewModel.setDynamicColor(it) }
                        )
                    }
                )
            }
        }

        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("Choose Theme") },
                text = {
                    Column {
                        ThemeOption(
                            text = "System Default",
                            selected = themeMode == ThemeMode.SYSTEM,
                            onClick = {
                                viewModel.setTheme(ThemeMode.SYSTEM)
                                showThemeDialog = false
                            }
                        )
                        ThemeOption(
                            text = "Light",
                            selected = themeMode == ThemeMode.LIGHT,
                            onClick = {
                                viewModel.setTheme(ThemeMode.LIGHT)
                                showThemeDialog = false
                            }
                        )
                        ThemeOption(
                            text = "Dark",
                            selected = themeMode == ThemeMode.DARK,
                            onClick = {
                                viewModel.setTheme(ThemeMode.DARK)
                                showThemeDialog = false
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ThemeOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(id = R.dimen.spacing_small))
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_xsmall)))
        Text(text)
    }
}
