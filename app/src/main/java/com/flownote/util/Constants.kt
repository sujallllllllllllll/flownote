package com.flownote.util

/**
 * App-wide constants
 */
object Constants {
    
    // Database
    const val DATABASE_NAME = "flownote_database"
    
    // Preferences
    const val PREFS_NAME = "flownote_prefs"
    const val PREF_THEME = "theme"
    const val PREF_AUTO_CATEGORIZE = "auto_categorize"
    const val PREF_DEFAULT_CATEGORY = "default_category"
    const val PREF_TEMP_NOTE_DURATION = "temp_note_duration"
    
    // Auto-save
    const val AUTO_SAVE_DELAY_MS = 500L
    
    // Search
    const val SEARCH_DEBOUNCE_MS = 300L
    const val MAX_RECENT_SEARCHES = 5
    
    // Temporary notes
    const val DEFAULT_TEMP_DURATION_DAYS = 7
    const val TEMP_NOTE_WARNING_DAYS = 1
    
    // UI
    const val MAX_TITLE_LENGTH = 100
    const val NOTE_PREVIEW_LENGTH = 150
    
    // Voice
    const val MAX_VOICE_DURATION_MS = 600000L // 10 minutes
}
