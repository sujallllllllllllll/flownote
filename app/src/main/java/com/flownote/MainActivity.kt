package com.flownote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.flownote.ui.MainScreen
import com.flownote.ui.theme.FlowNoteTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for FlowNote
 * Entry point of the app
 */
import androidx.activity.enableEdgeToEdge

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Allow rotation on tablets (sw600dp+), lock to portrait on phones
        val isTablet = resources.getBoolean(R.bool.is_tablet)
        if (isTablet) {
            requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
        // Portrait lock is already set in AndroidManifest for phones
        
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: com.flownote.ui.screens.settings.SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val useDynamicColors by settingsViewModel.useDynamicColors.collectAsState()

            val darkTheme = when (themeMode) {
                com.flownote.data.repository.ThemeMode.LIGHT -> false
                com.flownote.data.repository.ThemeMode.DARK -> true
                else -> isSystemInDarkTheme() // SYSTEM or default
            }

            FlowNoteTheme(
                darkTheme = darkTheme,
                dynamicColor = useDynamicColors
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Extract noteId from intent (for notification deep links)
                    val noteId = intent?.getStringExtra("note_id")
                    MainScreen(initialNoteId = noteId)
                }
            }
        }
    }
}
