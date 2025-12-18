package com.flownote.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flownote.data.model.Category
import com.flownote.data.model.Note
import com.flownote.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home screen
 * Manages notes list, search, and filtering
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Selected category filter
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()
    
    // Selected tags filter
    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())
    val selectedTags: StateFlow<List<String>> = _selectedTags.asStateFlow()
    
    // All notes from repository
    private val allNotes = noteRepository.getAllNotes()
    
    // Available tags from all notes
    val availableTags: StateFlow<List<String>> = allNotes.map { notes ->
        notes.flatMap { it.tags }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Filtered notes based on search and category
    val notes: StateFlow<List<Note>> = combine(
        allNotes,
        searchQuery,
        selectedCategory,
        selectedTags
    ) { notes, query, category, tags ->
        var filtered = notes
        
        // Filter by category
        if (category != null) {
            filtered = filtered.filter { it.category == category }
        }
        
        // Filter out empty/ghost notes
        filtered = filtered.filter { note -> 
             note.title.isNotBlank() || 
             note.getPlainTextContent().isNotBlank() || 
             note.hasAudio || 
             note.tags.isNotEmpty()
        }
        
        // Filter by tags
        if (tags.isNotEmpty()) {
            filtered = filtered.filter { note ->
                note.tags.containsAll(tags)
            }
        }
        
        // Filter by search query
        if (query.isNotBlank()) {
            filtered = filtered.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                note.content.contains(query, ignoreCase = true) ||
                note.tags.any { it.contains(query, ignoreCase = true) }
            }
        }
        
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    /**
     * Update search query
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Select category filter
     */
    fun onCategorySelected(category: Category?) {
        _selectedCategory.value = category
    }

    /**
     * Toggle tag selection
     */
    fun toggleTagFilter(tag: String) {
        val current = _selectedTags.value
        if (current.contains(tag)) {
            _selectedTags.value = current - tag
        } else {
            _selectedTags.value = current + tag
        }
    }

    /**
     * Create a new note from a template
     */
    suspend fun createNoteFromTemplate(template: com.flownote.data.model.Template): String {
        val newNote = Note(
            id = java.util.UUID.randomUUID().toString(),
            title = template.name,
            content = template.content,
            category = template.category,
            tags = emptyList(), // default
            isTemporary = false, // default
            deleteAfter = null, // default
            hasAudio = false, // default
            audioPath = null, // default
            createdAt = java.util.Date(),
            updatedAt = java.util.Date(),
            isSynced = false, // default
            isPinned = false, // default
            color = com.flownote.data.model.NoteColor.DEFAULT, // default
            reminderTime = null, // default
            isChecklist = template.id == "todo"
        )
        noteRepository.saveNote(newNote)
        return newNote.id
    }
    
    /**
     * Delete a note
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }
    
    /**
     * Toggle pin status
     */
    fun togglePin(noteId: String) {
        viewModelScope.launch {
            noteRepository.togglePin(noteId)
        }
    }
    
    /**
     * Clear search and filters
     */
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _selectedTags.value = emptyList()
    }
}
