package com.flownote.data.model

import java.util.Date
import java.util.UUID

/**
 * Domain model for a Note
 * This is the model used throughout the app (not the database entity)
 */
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val category: Category = Category.GENERAL,
    val tags: List<String> = emptyList(),
    val isTemporary: Boolean = false,
    val deleteAfter: Date? = null,
    val hasAudio: Boolean = false,
    val audioPath: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isSynced: Boolean = false,
    val isPinned: Boolean = false,
    val color: NoteColor = NoteColor.DEFAULT
) {
    /**
     * Generate title from content if title is empty
     */
    fun getDisplayTitle(): String {
        return if (title.isNotBlank()) {
            title
        } else {
            // Take first line or first 50 characters of content
            val firstLine = content.lines().firstOrNull()?.trim() ?: ""
            if (firstLine.length > 50) {
                firstLine.take(50) + "..."
            } else {
                firstLine.ifEmpty { "Untitled Note" }
            }
        }
    }

    /**
     * Check if note should be auto-deleted
     */
    fun shouldAutoDelete(): Boolean {
        return isTemporary && deleteAfter != null && Date().after(deleteAfter)
    }

    /**
     * Get days remaining until auto-delete
     */
    fun getDaysUntilDelete(): Int? {
        if (!isTemporary || deleteAfter == null) return null
        val diff = deleteAfter.time - Date().time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
}
