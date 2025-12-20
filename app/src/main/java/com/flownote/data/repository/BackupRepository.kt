package com.flownote.data.repository

import android.content.Context
import android.net.Uri
import com.flownote.data.model.Note
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for backup and restore operations
 * Handles exporting notes to ZIP and importing from ZIP
 */
@Singleton
class BackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val noteRepository: NoteRepository
) {
    
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateTypeAdapter())
        .setPrettyPrinting()
        .create()
    
    /**
     * Export all notes to a ZIP file
     * @param uri The Uri where the ZIP file should be written
     * @return Result indicating success or failure
     */
    suspend fun exportNotesToZip(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Get all notes from repository
            val notes = noteRepository.getAllNotesOneTime()
            
            // Convert notes to JSON
            val backupData = BackupData(
                version = BACKUP_VERSION,
                exportDate = Date().time,
                notes = notes
            )
            val json = gson.toJson(backupData)
            
            // Write to ZIP file
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                ZipOutputStream(outputStream).use { zipOut ->
                    val entry = ZipEntry(BACKUP_FILENAME)
                    zipOut.putNextEntry(entry)
                    zipOut.write(json.toByteArray())
                    zipOut.closeEntry()
                }
            } ?: return@withContext Result.failure(Exception("Failed to open output stream"))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Import notes from a ZIP file
     * @param uri The Uri of the ZIP file to import
     * @return Result with the number of notes imported
     */
    suspend fun importNotesFromZip(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val notes = mutableListOf<Note>()
            
            // Read from ZIP file
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
                    var entry = zipIn.nextEntry
                    while (entry != null) {
                        if (entry.name == BACKUP_FILENAME) {
                            val reader = BufferedReader(InputStreamReader(zipIn))
                            val json = reader.readText()
                            
                            val backupData = gson.fromJson(json, BackupData::class.java)
                            notes.addAll(backupData.notes)
                            break
                        }
                        entry = zipIn.nextEntry
                    }
                }
            } ?: return@withContext Result.failure(Exception("Failed to open input stream"))
            
            if (notes.isEmpty()) {
                return@withContext Result.failure(Exception("No notes found in backup file"))
            }
            
            // Save all notes to database
            notes.forEach { note ->
                noteRepository.saveNote(note)
            }
            
            Result.success(notes.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clear all notes from the database
     */
    suspend fun clearAllNotes(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            noteRepository.deleteAllNotes()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    companion object {
        private const val BACKUP_VERSION = 1
        private const val BACKUP_FILENAME = "flownotes_backup.json"
    }
    
    /**
     * Data class for backup structure
     */
    private data class BackupData(
        val version: Int,
        val exportDate: Long,
        val notes: List<Note>
    )
}

/**
 * Custom Gson TypeAdapter for Date serialization
 */
class DateTypeAdapter : com.google.gson.TypeAdapter<Date>() {
    override fun write(out: com.google.gson.stream.JsonWriter, value: Date?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.time)
        }
    }
    
    override fun read(input: com.google.gson.stream.JsonReader): Date? {
        return if (input.peek() == com.google.gson.stream.JsonToken.NULL) {
            input.nextNull()
            null
        } else {
            Date(input.nextLong())
        }
    }
}
