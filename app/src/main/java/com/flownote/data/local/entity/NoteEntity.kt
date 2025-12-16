package com.flownote.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.flownote.data.local.database.Converters
import java.util.Date

/**
 * Room database entity for notes
 * Stored in local SQLite database on the device
 */
@Entity(tableName = "notes")
@TypeConverters(Converters::class)
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val tags: List<String>,
    val isTemporary: Boolean,
    val deleteAfter: Long?, // Timestamp in milliseconds
    val hasAudio: Boolean,
    val audioPath: String?,
    val createdAt: Long, // Timestamp in milliseconds
    val updatedAt: Long, // Timestamp in milliseconds
    val isSynced: Boolean,
    val isPinned: Boolean,
    val color: String,
    val reminderTime: Long?, // Timestamp in milliseconds
    val isChecklist: Boolean = false
)
