package com.flownote.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Preference Keys
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val USE_DYNAMIC_COLORS = booleanPreferencesKey("use_dynamic_colors")
    }

    // Get Theme Mode
    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            try {
                val mode = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
                ThemeMode.valueOf(mode)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }
        }

    // Get Dynamic Color Preference
    val useDynamicColors: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_COLORS] ?: true // Default true for Android 12+
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_COLORS] = enabled
        }
    }
}
