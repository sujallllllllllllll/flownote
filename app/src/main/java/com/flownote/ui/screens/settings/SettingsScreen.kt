package com.flownote.ui.screens.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.flownote.R
import com.flownote.data.repository.ThemeMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToContactUs: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToUpcomingFeatures: () -> Unit = {}
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val useDynamicColors by viewModel.useDynamicColors.collectAsState()
    val backupState by viewModel.backupState.collectAsState()
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    // File picker for export
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { viewModel.exportBackup(it) }
    }

    // File picker for import
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importBackup(it) }
    }

    // Handle backup state changes
    LaunchedEffect(backupState) {
        when (backupState) {
            is BackupState.ExportSuccess -> {
                // Show dialog asking to clear data
                showClearDataDialog = true
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = dimensionResource(id = R.dimen.spacing_medium)) // 16dp bottom breathing space
        ) {
            // HIDDEN FOR MVP LAUNCH - Uncomment to enable Backup & Restore
            /*
            // Backup & Restore Section
            Text(
                text = "Backup & Restore",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.screen_margin_horizontal), 
                    top = dimensionResource(id = R.dimen.spacing_medium), 
                    bottom = dimensionResource(id = R.dimen.spacing_xsmall)
                )
            )

            ListItem(
                headlineContent = { Text("Export Backup") },
                supportingContent = { Text("Save all notes to a ZIP file") },
                leadingContent = {
                    Icon(Icons.Default.Upload, contentDescription = null)
                },
                modifier = Modifier.clickable {
                    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    val fileName = "FlowNotes_Backup_${dateFormat.format(Date())}.zip"
                    exportLauncher.launch(fileName)
                }
            )

            ListItem(
                headlineContent = { Text("Import Backup") },
                supportingContent = { Text("Restore notes from a ZIP file") },
                leadingContent = {
                    Icon(Icons.Default.Download, contentDescription = null)
                },
                modifier = Modifier.clickable {
                    importLauncher.launch(arrayOf("application/zip"))
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_small)))
            */

            // About Section
            Text(
                text = "About",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.screen_margin_horizontal),
                    top = dimensionResource(id = R.dimen.spacing_medium),
                    bottom = dimensionResource(id = R.dimen.spacing_xsmall)
                )
            )

            ListItem(
                headlineContent = { Text("Contact Us") },
                supportingContent = { Text("Get in touch with feedback or questions") },
                leadingContent = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                modifier = Modifier.clickable {
                    onNavigateToContactUs()
                }
            )

            ListItem(
                headlineContent = { Text("Privacy Policy") },
                supportingContent = { Text("Learn how we protect your data") },
                leadingContent = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                modifier = Modifier.clickable {
                    onNavigateToPrivacyPolicy()
                }
            )

            ListItem(
                headlineContent = { Text("Upcoming Features") },
                supportingContent = { Text("Preview what's being explored") },
                leadingContent = {
                    Icon(Icons.Default.Lightbulb, contentDescription = null)
                },
                modifier = Modifier.clickable {
                    onNavigateToUpcomingFeatures()
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_xsmall))) // 8dp - tighter

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

            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_xsmall))) // 8dp - tighter
        }

        // Theme Dialog
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

        // Clear Data Confirmation Dialog (after export)
        if (showClearDataDialog) {
            AlertDialog(
                onDismissRequest = {
                    showClearDataDialog = false
                    viewModel.resetBackupState()
                },
                icon = { Icon(Icons.Default.Upload, contentDescription = null) },
                title = { Text("Backup Exported Successfully") },
                text = { Text("Your backup has been saved. Do you want to clear all app data now?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearDataDialog = false
                            viewModel.clearAllData()
                        }
                    ) {
                        Text("Yes, Clear Data")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showClearDataDialog = false
                            viewModel.resetBackupState()
                        }
                    ) {
                        Text("No, Keep Data")
                    }
                }
            )
        }

        // Loading Dialog
        if (backupState is BackupState.Loading) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Please wait") },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text("Processing...")
                    }
                },
                confirmButton = {}
            )
        }

        // Import Success Dialog
        if (backupState is BackupState.ImportSuccess) {
            val noteCount = (backupState as BackupState.ImportSuccess).noteCount
            AlertDialog(
                onDismissRequest = { viewModel.resetBackupState() },
                icon = { Icon(Icons.Default.Download, contentDescription = null) },
                title = { Text("Import Successful") },
                text = { Text("Successfully imported $noteCount note${if (noteCount != 1) "s" else ""}.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetBackupState() }) {
                        Text("OK")
                    }
                }
            )
        }

        // Clear Data Success Dialog
        if (backupState is BackupState.ClearSuccess) {
            AlertDialog(
                onDismissRequest = { viewModel.resetBackupState() },
                title = { Text("Data Cleared") },
                text = { Text("All notes have been removed from the app.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetBackupState() }) {
                        Text("OK")
                    }
                }
            )
        }

        // Error Dialog
        if (backupState is BackupState.Error) {
            val errorMessage = (backupState as BackupState.Error).message
            AlertDialog(
                onDismissRequest = { viewModel.resetBackupState() },
                title = { Text("Error") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetBackupState() }) {
                        Text("OK")
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
