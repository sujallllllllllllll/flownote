package com.flownote.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flownote.data.local.dao.NoteDao
import com.flownote.data.local.entity.NoteEntity

/**
 * Room database for FlowNote
 * All data stored locally on device (100% offline)
 */
@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlowNoteDatabase : RoomDatabase() {
    
    /**
     * Get the NoteDao for database operations
     */
    abstract fun noteDao(): NoteDao
    
    companion object {
        const val DATABASE_NAME = "flownote_database"
    }
}
