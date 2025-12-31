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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home screen
 * Manages notes list, search, and filtering
 * Optimized version using database-side filtering
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Debounced search query (300ms delay to reduce database queries)
    private val debouncedSearchQuery = _searchQuery
        .debounce(300) // Wait 300ms after user stops typing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )
    
    // Selected category filter
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()
    
    // Selected tags filter (client-side for now, can be optimized later)
    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())
    val selectedTags: StateFlow<List<String>> = _selectedTags.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // All notes from repository (using optimized query with pagination)
    private val filteredNotesFlow = combine(
        selectedCategory,
        debouncedSearchQuery, // Use debounced query instead of immediate
        selectedTags
    ) { category, query, tags ->
        Triple(category, query, tags)
    }.flatMapLatest { (category, query, tags) ->
        noteRepository.getFilteredNotes(
            category = category,
            query = query,
            limit = 50 // Load 50 notes at a time for better performance
        ).map { notes ->
            // Apply tag filtering (client-side for now)
            if (tags.isNotEmpty()) {
                notes.filter { note -> note.tags.containsAll(tags) }
            } else {
                notes
            }
        }
    }
    
    // Available tags from all notes
    val availableTags: StateFlow<List<String>> = noteRepository.getAllNotes().map { notes ->
        notes.flatMap { it.tags }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // UI State separating pinned and other notes
    val uiState: StateFlow<HomeUiState> = filteredNotesFlow.map { notes ->
        // Separate pinned and other notes
        val pinnedNotes = notes.filter { it.isPinned }
        val otherNotes = notes.filter { !it.isPinned }
        
        HomeUiState(
            pinnedNotes = pinnedNotes,
            otherNotes = otherNotes,
            allNotes = notes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
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
            try {
                noteRepository.deleteNote(note)
            } catch (e: Exception) {
                _error.value = "Failed to delete note. Please try again."
            }
        }
    }
    
    /**
     * Toggle pin status
     */
    fun togglePin(noteId: String) {
        viewModelScope.launch {
            try {
                noteRepository.togglePin(noteId)
            } catch (e: Exception) {
                _error.value = "Failed to update note. Please try again."
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
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

/**
 * UI State for Home screen
 * Separates pinned and other notes for easier UI rendering
 */
data class HomeUiState(
    val pinnedNotes: List<Note> = emptyList(),
    val otherNotes: List<Note> = emptyList(),
    val allNotes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
