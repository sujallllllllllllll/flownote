package com.flownote.ui.screens.addedit

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flownote.data.model.Category
import com.flownote.data.model.Note
import com.flownote.data.model.NoteColor
import com.flownote.data.repository.NoteRepository
import com.flownote.util.NotificationScheduler
import com.flownote.util.PdfExporter
import com.flownote.util.VoiceRecorder
import com.flownote.util.SpeechToTextManager
import com.flownote.util.ExportUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for Note Editor screen
 */
@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {



    private val notificationScheduler = NotificationScheduler()
    val speechManager = SpeechToTextManager(context)
    private val voiceRecorder = VoiceRecorder()

    private val noteId: String? = savedStateHandle["noteId"]
    
    // State
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    
    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()
    
    private val _category = MutableStateFlow(Category.GENERAL)
    val category: StateFlow<Category> = _category.asStateFlow()
    
    private val _noteColor = MutableStateFlow(NoteColor.DEFAULT)
    val noteColor: StateFlow<NoteColor> = _noteColor.asStateFlow()
    
    private val _isPinned = MutableStateFlow(false)
    val isPinned: StateFlow<Boolean> = _isPinned.asStateFlow()
    

    
    private val _lastEdited = MutableStateFlow<String?>(null)
    val lastEdited: StateFlow<String?> = _lastEdited.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isNoteSaved = MutableStateFlow(false)
    val isNoteSaved: StateFlow<Boolean> = _isNoteSaved.asStateFlow()
    
    private val _reminderTime = MutableStateFlow<Date?>(null)
    val reminderTime: StateFlow<Date?> = _reminderTime.asStateFlow()

    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()
    
    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()
    
    private val _hasAudio = MutableStateFlow(false)
    val hasAudio: StateFlow<Boolean> = _hasAudio.asStateFlow()
    
    private val _audioPath = MutableStateFlow<String?>(null)
    val audioPath: StateFlow<String?> = _audioPath.asStateFlow()

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags: StateFlow<List<String>> = _tags.asStateFlow()
    
    // Temporary Note State
    private val _isTemporary = MutableStateFlow(false)
    val isTemporary: StateFlow<Boolean> = _isTemporary.asStateFlow()
    
    private val _deleteAfter = MutableStateFlow<Date?>(null)
    val deleteAfter: StateFlow<Date?> = _deleteAfter.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    val speechState = speechManager.speechState
    
    private var currentNote: Note? = null
    
    // Auto-save state
    private var autoSaveJob: Job? = null
    private var isDeleted = false // Prevent resurrection after deletion

    init {
        if (noteId != null && noteId != "new") {
            loadNote(noteId)
        }
    }
    
    // Robust empty check (strips HTML)
    private val isNoteEmpty: Boolean
        get() {
            val plainContent = android.text.Html.fromHtml(_content.value, android.text.Html.FROM_HTML_MODE_COMPACT).toString().trim()
            val isContentEmpty = plainContent.isBlank()
            val isTitleEmpty = _title.value.isBlank()
            return isContentEmpty && isTitleEmpty
        }

    
    private fun loadNote(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val note = noteRepository.getNoteById(id)
                note?.let {
                    currentNote = it
                    _title.value = it.title
                    _content.value = it.content
                    _category.value = it.category
                    _noteColor.value = it.color
                    _isPinned.value = it.isPinned
                    _reminderTime.value = it.reminderTime
                    _hasAudio.value = it.hasAudio
                    _audioPath.value = it.audioPath
                    _tags.value = it.tags
                    _isTemporary.value = it.isTemporary
                    _deleteAfter.value = it.deleteAfter
                    _lastEdited.value = com.flownote.util.DateUtils.formatDate(it.updatedAt)
                }
            } catch (e: Exception) {
                _error.value = "Failed to load note. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun onTitleChange(newTitle: String) {
        // Limit title to 200 characters
        val validatedTitle = if (newTitle.length > 200) {
            _error.value = "Title cannot exceed 200 characters"
            newTitle.take(200)
        } else {
            newTitle
        }
        _title.value = validatedTitle
        triggerAutoSave() // Auto-save after title change
    }
    
    fun onContentChange(newContent: String) {
        _content.value = newContent
        triggerAutoSave() // Auto-save after content change
    }
    
    fun onCategoryChange(newCategory: Category) {
        _category.value = newCategory
    }
    
    fun onColorChange(newColor: NoteColor) {
        _noteColor.value = newColor
    }
    
    fun togglePin() {
        _isPinned.value = !_isPinned.value
        // Auto-save when pinning/unpinning
        saveNote(navigateBack = false)
    }
    

    
    fun setReminder(date: Date?) {
        _reminderTime.value = date
        // Auto-save when setting reminder
        saveNote(navigateBack = false)
    }
    
    fun setAsTemporary(isTemp: Boolean) {
        _isTemporary.value = isTemp
        if (!isTemp) {
            _deleteAfter.value = null
        }
    }
    
    fun setExpirationDate(date: Date) {
        _deleteAfter.value = date
        _isTemporary.value = true
    }
    
    fun removeExpiration() {
        _isTemporary.value = false
        _deleteAfter.value = null
    }
    
    fun deleteNote() {
        // Prevent re-entry or conflicts
        if (isDeleted) return
        
        currentNote?.let {
            // Cancel any pending auto-save
            autoSaveJob?.cancel()
            isDeleted = true // Mark as deleted to prevent resurrection
            
            viewModelScope.launch {
                try {
                    noteRepository.deleteNote(it)
                    _isNoteSaved.value = true // Trigger navigation back
                } catch (e: Exception) {
                    _error.value = "Failed to delete note. Please try again."
                    isDeleted = false // Reset on error
                }
            }
        }
    }
    
    /**
     * Trigger auto-save with 500ms debounce
     */
    private fun triggerAutoSave() {
        // Cancel any pending auto-save
        autoSaveJob?.cancel()
        
        // Schedule new auto-save with 500ms debounce
        autoSaveJob = viewModelScope.launch {
            delay(500L) // 500ms debounce (reduced from 1000ms)
            saveNote(navigateBack = false) // Silent save, no navigation
        }
    }
    
    /**
     * Force immediate save and flush any pending auto-save
     */
    /**
     * Force immediate save and flush any pending auto-save
     * Called when leaving the screen
     */
    fun flushAutoSave() {
        autoSaveJob?.cancel()
        
        // If manually deleted, DO NOT FLUSH (prevents resurrection)
        if (isDeleted) return
        
        // EXIT STRATEGY:
        // 1. If EXISTING note is empty -> DELETE it (Cleanup)
        // 2. Otherwise -> Save (or don't create new if empty)
        if (currentNote != null && isNoteEmpty) {
            deleteNote()
        } else {
            saveNote(navigateBack = false)
        }
    }
    
    fun saveNote(navigateBack: Boolean = true) {
        // Cancel pending auto-save to prevent duplicate
        autoSaveJob?.cancel()
        
        // If manually deleted, DO NOT SAVE
        if (isDeleted) return
        
        // Check if note is effectively empty
        if (isNoteEmpty) {
             // 1. NEW Note -> Do NOT create (return).
             // 2. EXISTING Note -> Allow Save (Persist empty state while editing).
             // It will be deleted on exit by flushAutoSave.
             if (currentNote == null) {
                 return
             }
        }
        
        viewModelScope.launch {
            try {
                val note = currentNote?.copy(
                    title = _title.value,
                    content = _content.value,
                    category = _category.value,
                    tags = _tags.value,
                    color = _noteColor.value,
                    isPinned = _isPinned.value,
                    reminderTime = _reminderTime.value,
                    hasAudio = _hasAudio.value,
                    audioPath = _audioPath.value,
                    isTemporary = _isTemporary.value,
                    deleteAfter = _deleteAfter.value,
                    updatedAt = java.util.Date()
                ) ?: Note(
                    id = UUID.randomUUID().toString(),
                    title = _title.value,
                    content = _content.value,
                    category = _category.value,
                    tags = _tags.value,
                    isTemporary = _isTemporary.value,
                    deleteAfter = _deleteAfter.value,
                    hasAudio = _hasAudio.value,
                    audioPath = _audioPath.value,
                    createdAt = java.util.Date(),
                    updatedAt = java.util.Date(),
                    isSynced = false,
                    isPinned = _isPinned.value,
                    color = _noteColor.value,
                    reminderTime = _reminderTime.value,
                    isChecklist = false
                )
                
                // Schedule or Cancel Reminder
                if (_reminderTime.value != null && _reminderTime.value!!.after(Date())) {
                    notificationScheduler.scheduleReminder(
                        context, 
                        note.id, 
                        note.title.ifBlank { "Untitled Note" },
                        _reminderTime.value!!
                    )
                } else {
                    notificationScheduler.cancelReminder(context, note.id)
                }
                
                noteRepository.saveNote(note)
                
                // CRITICAL FIX: Update currentNote after first save
                // This prevents duplicate notes on subsequent auto-saves
                if (currentNote == null) {
                    currentNote = note
                }
                
                // Only trigger navigation if requested
                if (navigateBack) {
                    _isNoteSaved.value = true
                }
            } catch (e: Exception) {
                _error.value = "Failed to save note. Please try again."
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
    
    fun addTag(tag: String) {
        // Trim whitespace and validate
        val trimmedTag = tag.trim()
        
        // Validate tag
        if (trimmedTag.isEmpty()) {
            _error.value = "Tag cannot be empty"
            return
        }
        
        if (trimmedTag.length > 50) {
            _error.value = "Tag cannot exceed 50 characters"
            return
        }
        
        // Check for duplicates (case-insensitive)
        if (_tags.value.any { it.equals(trimmedTag, ignoreCase = true) }) {
            _error.value = "Tag already exists"
            return
        }
        
        _tags.value = _tags.value + trimmedTag
    }

    fun removeTag(tag: String) {
        _tags.value = _tags.value.filter { it != tag }
    }

    fun toggleAudioRecording() {
        if (_isRecordingAudio.value) {
            // Stop Recording
            try {
                val file = voiceRecorder.stopRecording()
                _isRecordingAudio.value = false
                if (file != null) {
                    _hasAudio.value = true
                    _audioPath.value = file.absolutePath
                    viewModelScope.launch {
                        saveNote(navigateBack = false) // Save audio attachment
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isRecordingAudio.value = false
            }
        } else {
            // Start Recording
            try {
                // Create file
                val audioDir = File(context.filesDir, "audio_notes")
                if (!audioDir.exists()) audioDir.mkdirs()
                val file = File(audioDir, "audio_${UUID.randomUUID()}.aac")
                
                voiceRecorder.startRecording(context, file)
                _isRecordingAudio.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun toggleAudioPlayback() {
        if (_isPlayingAudio.value) {
            voiceRecorder.stopPlayback()
            _isPlayingAudio.value = false
        } else {
            _audioPath.value?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    try {
                        voiceRecorder.playAudio(file) {
                            _isPlayingAudio.value = false
                        }
                        _isPlayingAudio.value = true
                    } catch (e: Exception) {
                         e.printStackTrace()
                    }
                }
            }
        }
    }
    
    fun deleteAudio() {
        _hasAudio.value = false
        _audioPath.value = null
        // Optionally delete file, but keep it simple for now (or delete later)
        saveNote(navigateBack = false)
    }
    
    fun exportAsText() {
        val note = currentNote?.copy(
             title = _title.value,
             content = _content.value
        ) ?: Note(title = _title.value, content = _content.value)
        
        ExportUtils.exportAsText(context, note)
    }
    
    fun exportAsPdf() {
         val note = currentNote?.copy(
             title = _title.value,
             content = _content.value
        ) ?: Note(title = _title.value, content = _content.value)
        
        ExportUtils.exportAsPdf(context, note)
    }
    
    override fun onCleared() {
        super.onCleared()
        // Cancel auto-save on ViewModel cleanup
        autoSaveJob?.cancel()
        try {
            voiceRecorder.release()
            speechManager.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}