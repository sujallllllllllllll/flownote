package com.flownote.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flownote.data.model.Category
import com.flownote.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for Categories Screen
 */
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    /**
     * Category with note count
     */
    data class CategoryWithCount(
        val category: Category,
        val noteCount: Int
    )

    /**
     * Get all categories with their note counts
     */
    val categoriesWithCounts: StateFlow<List<CategoryWithCount>> = noteRepository.getAllNotes()
        .map { notes ->
            Category.values().map { category ->
                CategoryWithCount(
                    category = category,
                    noteCount = notes.count { it.category == category }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
