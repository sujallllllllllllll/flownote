package com.flownote.data.repository

import com.flownote.data.local.dao.NoteDao
import com.flownote.data.local.entity.NoteEntity
import com.flownote.data.model.Category
import com.flownote.data.model.Note
import com.flownote.data.model.NoteColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for note data operations
 * Abstracts data source and provides clean API for ViewModels
 * Handles conversion between Entity (database) and Model (domain)
 */
@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    
    /**
     * Get all notes as Flow
     */
    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }
    
    /**
     * Get note by ID
     */
    suspend fun getNoteById(noteId: String): Note? {
        return noteDao.getNoteById(noteId)?.toNote()
    }
    
    /**
     * Search notes
     */
    fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { entities ->
            entities.map { it.toNote() }
        }
    }
    
    /**
     * Get notes by category
     */
    fun getNotesByCategory(category: Category): Flow<List<Note>> {
        return noteDao.getNotesByCategory(category.displayName).map { entities ->
            entities.map { it.toNote() }
        }
    }
    
    /**
     * Get filtered notes (optimized database query)
     */
    fun getFilteredNotes(
        category: Category?,
        query: String
    ): Flow<List<Note>> {
        return noteDao.getFilteredNotes(
            category = category?.displayName,
            query = query
        ).map { entities ->
            entities.map { it.toNote() }
        }
    }
    
    /**
     * Get temporary notes
     */
    fun getTemporaryNotes(): Flow<List<Note>> {
        return noteDao.getTemporaryNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }
    
    /**
     * Get pinned notes
     */
    fun getPinnedNotes(): Flow<List<Note>> {
        return noteDao.getPinnedNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }
    
    /**
     * Insert or update a note
     */
    suspend fun saveNote(note: Note) {
        noteDao.insertNote(note.toEntity())
    }
    
    /**
     * Delete a note
     */
    suspend fun deleteNote(note: Note) {
        noteDao.deleteNoteById(note.id)
    }
    
    /**
     * Delete note by ID
     */
    suspend fun deleteNoteById(noteId: String) {
        noteDao.deleteNoteById(noteId)
    }
    
    /**
     * Toggle pin status
     */
    suspend fun togglePin(noteId: String) {
        val note = noteDao.getNoteById(noteId)
        note?.let {
            val updated = it.copy(
                isPinned = !it.isPinned,
                updatedAt = System.currentTimeMillis()
            )
            noteDao.updateNote(updated)
        }
    }
    
    /**
     * Update note category
     */
    suspend fun updateCategory(noteId: String, category: Category) {
        val note = noteDao.getNoteById(noteId)
        note?.let {
            val updated = it.copy(
                category = category.displayName,
                updatedAt = System.currentTimeMillis()
            )
            noteDao.updateNote(updated)
        }
    }
    
    /**
     * Delete expired temporary notes
     */
    suspend fun deleteExpiredNotes() {
        val currentTime = System.currentTimeMillis()
        val expiredNotes = noteDao.getNotesToDelete(currentTime)
        expiredNotes.forEach { noteDao.deleteNote(it) }
    }
    
    /**
     * Get note count
     */
    suspend fun getNoteCount(): Int {
        return noteDao.getNoteCount()
    }
    
    /**
     * Get count by category
     */
    suspend fun getCountByCategory(category: Category): Int {
        return noteDao.getCountByCategory(category.displayName)
    }
    
    /**
     * Get all notes as a one-time list (for backup)
     */
    suspend fun getAllNotesOneTime(): List<Note> {
        return noteDao.getAllNotesOneTime().map { it.toNote() }
    }
    
    /**
     * Delete all notes (for clearing data)
     */
    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
    
    // Extension functions for conversion
    
    /**
     * Convert NoteEntity to Note (domain model)
     */
    private fun NoteEntity.toNote(): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            category = Category.fromDisplayName(category),
            tags = tags,
            isTemporary = isTemporary,
            deleteAfter = deleteAfter?.let { Date(it) },
            hasAudio = hasAudio,
            audioPath = audioPath,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt),
            isSynced = isSynced,
            isPinned = isPinned,
            color = NoteColor.fromHex(color),
            reminderTime = reminderTime?.let { Date(it) },
            isChecklist = isChecklist
        )
    }
    
    /**
     * Convert Note to NoteEntity (database model)
     */
    private fun Note.toEntity(): NoteEntity {
        return NoteEntity(
            id = id,
            title = title,
            content = content,
            category = category.displayName,
            tags = tags,
            isTemporary = isTemporary,
            deleteAfter = deleteAfter?.time,
            hasAudio = hasAudio,
            audioPath = audioPath,
            createdAt = createdAt.time,
            updatedAt = updatedAt.time,
            isSynced = isSynced,
            isPinned = isPinned,
            color = color.hexValue,
            reminderTime = reminderTime?.time,
            isChecklist = isChecklist
        )
    }
}
