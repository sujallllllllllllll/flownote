package com.flownote.data.local.dao

import androidx.room.*
import com.flownote.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for notes
 * All database operations for notes
 */
@Dao
interface NoteDao {
    
    /**
     * Get all notes as Flow (reactive updates)
     * Pinned notes appear first, then sorted by update time
     */
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>
    
    /**
     * Get a single note by ID
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?
    
    /**
     * Search notes by title or content
     * Uses LIKE for simple text matching
     */
    @Query("""
        SELECT * FROM notes 
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%'
        ORDER BY isPinned DESC, updatedAt DESC
    """)
    fun searchNotes(query: String): Flow<List<NoteEntity>>
    
    /**
     * Get notes by category
     */
    @Query("SELECT * FROM notes WHERE category = :category ORDER BY isPinned DESC, updatedAt DESC")
    fun getNotesByCategory(category: String): Flow<List<NoteEntity>>
    
    /**
     * Get temporary notes
     */
    @Query("SELECT * FROM notes WHERE isTemporary = 1 ORDER BY deleteAfter ASC")
    fun getTemporaryNotes(): Flow<List<NoteEntity>>
    
    /**
     * Get notes that should be auto-deleted
     */
    @Query("SELECT * FROM notes WHERE isTemporary = 1 AND deleteAfter <= :currentTime")
    suspend fun getNotesToDelete(currentTime: Long): List<NoteEntity>
    
    /**
     * Get pinned notes
     */
    @Query("SELECT * FROM notes WHERE isPinned = 1 ORDER BY updatedAt DESC")
    fun getPinnedNotes(): Flow<List<NoteEntity>>
    
    /**
     * Insert a new note
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)
    
    /**
     * Insert multiple notes
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)
    
    /**
     * Update an existing note
     */
    @Update
    suspend fun updateNote(note: NoteEntity)
    
    /**
     * Delete a note
     */
    @Delete
    suspend fun deleteNote(note: NoteEntity)
    
    /**
     * Delete note by ID
     */
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String)
    
    /**
     * Delete all notes (for testing/reset)
     */
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
    
    /**
     * Get total note count
     */
    @Query("SELECT COUNT(*) FROM notes")
    suspend fun getNoteCount(): Int
    
    /**
     * Get count by category
     */
    @Query("SELECT COUNT(*) FROM notes WHERE category = :category")
    suspend fun getCountByCategory(category: String): Int
}
