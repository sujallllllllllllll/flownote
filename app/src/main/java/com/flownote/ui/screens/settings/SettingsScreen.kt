package com.flownote.ui.screens.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
    onNavigateToContactUs: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToUpcomingFeatures: () -> Unit,
    onNavigateToHelp: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val useDynamicColors by viewModel.useDynamicColors.collectAsState()
    val backupState by viewModel.backupState.collectAsState()
    
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
                showClearDataDialog = true
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = dimensionResource(id = R.dimen.spacing_medium))
        ) {
            // APPEARANCE Section
            SectionHeader("APPEARANCE")
            
            // Theme Selector Pills
            ThemeSelector(
                currentTheme = themeMode,
                onThemeSelected = { viewModel.setTheme(it) }
            )
            
            Text(
                text = "FlowNotes will automatically adjust to your device's appearance settings.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.screen_margin_horizontal),
                    vertical = dimensionResource(id = R.dimen.spacing_small)
                )
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
            
            // OFFLINE-FIRST MODE Indicator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.screen_margin_horizontal)),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.spacing_medium)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_medium)))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_offline_mode),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.settings_offline_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

            // SUPPORT & COMMUNITY Section
            SectionHeader("SUPPORT & COMMUNITY")
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.screen_margin_horizontal)),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                SettingsItem(
                    icon = Icons.Default.Lightbulb,
                    iconTint = Color(0xFF8B5CF6),
                    title = "Upcoming Features",
                    subtitle = "See what's coming next",
                    onClick = onNavigateToUpcomingFeatures
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.spacing_xlarge)),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
                
                SettingsItem(
                    icon = Icons.Default.Favorite,
                    iconTint = Color(0xFFEC4899),
                    title = "Support FlowNotes",
                    subtitle = "Help keep this app free and ad-free",
                    onClick = { 
                        // Open browser to donation page
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                            data = android.net.Uri.parse("https://flownotes-presencematic.netlify.app/donate")
                        }
                        context.startActivity(intent)
                    }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.spacing_xlarge)),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
                
                SettingsItem(
                    icon = Icons.Default.Email,
                    iconTint = Color(0xFF3B82F6),
                    title = "Contact & Help",
                    subtitle = "Get in touch with feedback or questions",
                    onClick = onNavigateToContactUs
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.spacing_xlarge)),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
                
                SettingsItem(
                    icon = Icons.Default.Help,
                    iconTint = Color(0xFF10B981),
                    title = "Help & Guide",
                    subtitle = "Learn how to use FlowNotes",
                    onClick = onNavigateToHelp
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

            // ABOUT Section
            SectionHeader("ABOUT")
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.screen_margin_horizontal)),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                SettingsItem(
                    icon = Icons.Default.Lock,
                    iconTint = Color(0xFF10B981),
                    title = "Privacy Policy",
                    subtitle = "Learn how we protect your data",
                    onClick = onNavigateToPrivacyPolicy
                )
                
                // TODO: Uncomment when app is on Play Store
                /*
                HorizontalDivider(
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.spacing_xlarge)),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
                
                SettingsItem(
                    icon = Icons.Default.Star,
                    iconTint = Color(0xFFFBBF24),
                    title = "Rate on App Store",
                    subtitle = "Share your experience",
                    onClick = { /* Rate action */ }
                )
                */
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            // App Version Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.spacing_medium)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "FlowNotes",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                
                Text(
                    text = "FlowNotes v1.0.0 (Build 142)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Offline-first & Private",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        // Dialogs
        BackupDialogs(
            backupState = backupState,
            showClearDataDialog = showClearDataDialog,
            onDismissClearDialog = {
                showClearDataDialog = false
                viewModel.resetBackupState()
            },
            onClearData = {
                showClearDataDialog = false
                viewModel.clearAllData()
            },
            onResetBackupState = { viewModel.resetBackupState() }
        )
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.padding(
            start = dimensionResource(id = R.dimen.screen_margin_horizontal),
            top = dimensionResource(id = R.dimen.spacing_medium),
            bottom = dimensionResource(id = R.dimen.spacing_small)
        )
    )
}

@Composable
fun ThemeSelector(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.screen_margin_horizontal)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
    ) {
        ThemePill(
            icon = Icons.Default.LightMode,
            label = "Light",
            selected = currentTheme == ThemeMode.LIGHT,
            onClick = { onThemeSelected(ThemeMode.LIGHT) },
            modifier = Modifier.weight(1f)
        )
        
        ThemePill(
            icon = Icons.Default.DarkMode,
            label = "Dark",
            selected = currentTheme == ThemeMode.DARK,
            onClick = { onThemeSelected(ThemeMode.DARK) },
            modifier = Modifier.weight(1f)
        )
        
        ThemePill(
            icon = Icons.Default.PhoneAndroid,
            label = "System",
            selected = currentTheme == ThemeMode.SYSTEM,
            onClick = { onThemeSelected(ThemeMode.SYSTEM) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ThemePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = if (!selected) ButtonDefaults.outlinedButtonBorder else null,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(dimensionResource(id = R.dimen.spacing_medium)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_medium)))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun BackupDialogs(
    backupState: BackupState,
    showClearDataDialog: Boolean,
    onDismissClearDialog: () -> Unit,
    onClearData: () -> Unit,
    onResetBackupState: () -> Unit
) {
    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = onDismissClearDialog,
            icon = { Icon(Icons.Default.Upload, contentDescription = null) },
            title = { Text("Backup Exported Successfully") },
            text = { Text("Your backup has been saved. Do you want to clear all app data now?") },
            confirmButton = {
                TextButton(onClick = onClearData) {
                    Text("Yes, Clear Data")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissClearDialog) {
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
            onDismissRequest = onResetBackupState,
            icon = { Icon(Icons.Default.Download, contentDescription = null) },
            title = { Text("Import Successful") },
            text = { Text("Successfully imported $noteCount note${if (noteCount != 1) "s" else ""}.") },
            confirmButton = {
                TextButton(onClick = onResetBackupState) {
                    Text("OK")
                }
            }
        )
    }

    // Clear Data Success Dialog
    if (backupState is BackupState.ClearSuccess) {
        AlertDialog(
            onDismissRequest = onResetBackupState,
            title = { Text("Data Cleared") },
            text = { Text("All notes have been removed from the app.") },
            confirmButton = {
                TextButton(onClick = onResetBackupState) {
                    Text("OK")
                }
            }
        )
    }

    // Error Dialog
    if (backupState is BackupState.Error) {
        val errorMessage = (backupState as BackupState.Error).message
        AlertDialog(
            onDismissRequest = onResetBackupState,
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = onResetBackupState) {
                    Text("OK")
                }
            }
        )
    }
}
