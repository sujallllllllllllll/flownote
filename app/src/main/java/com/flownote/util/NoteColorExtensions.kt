package com.flownote.util

import androidx.compose.ui.graphics.Color
import com.flownote.data.model.NoteColor

/**
 * Extension to get adaptive color for dark mode
 */
fun NoteColor.getAdaptiveColor(isDarkTheme: Boolean): Color {
    val hex = if (isDarkTheme) {
        when (this) {
            NoteColor.DEFAULT -> "#1C1B1F"
            NoteColor.YELLOW -> "#4A4628"
            NoteColor.GREEN -> "#203824"
            NoteColor.BLUE -> "#1F3142"
            NoteColor.PURPLE -> "#39263D"
            NoteColor.PINK -> "#402532"
            NoteColor.ORANGE -> "#422E1C"
        }
    } else {
        this.hexValue
    }
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.White
    }
}
