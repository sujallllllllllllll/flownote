package com.flownote.util

import androidx.compose.ui.graphics.Color

/**
 * Extension functions for common operations
 */

/**
 * Convert hex color string to Compose Color
 */
fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: IllegalArgumentException) {
        Color.White
    }
}

/**
 * Truncate string to max length with ellipsis
 */
fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.take(maxLength) + "..."
    } else {
        this
    }
}

/**
 * Extract hashtags from text
 */
fun String.extractHashtags(): List<String> {
    val regex = "#\\w+".toRegex()
    return regex.findAll(this).map { it.value }.toList()
}

/**
 * Extract mentions from text
 */
fun String.extractMentions(): List<String> {
    val regex = "@\\w+".toRegex()
    return regex.findAll(this).map { it.value }.toList()
}

/**
 * Check if string contains any of the keywords (case-insensitive)
 */
fun String.containsAny(keywords: List<String>, ignoreCase: Boolean = true): Boolean {
    return keywords.any { this.contains(it, ignoreCase) }
}

/**
 * Count occurrences of a substring
 */
fun String.countOccurrences(substring: String, ignoreCase: Boolean = true): Int {
    return this.split(substring, ignoreCase = ignoreCase).size - 1
}
