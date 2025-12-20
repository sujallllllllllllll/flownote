package com.flownote.ui

import androidx.lifecycle.ViewModel
import com.flownote.data.model.Note
import com.flownote.data.model.Template
import com.flownote.data.model.Category
import com.flownote.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    suspend fun createNoteFromTemplate(template: Template): String {
        val newNote = Note(
            title = template.name,
            content = template.content,
            category = template.category,
            createdAt = java.util.Date(),
            updatedAt = java.util.Date()
        )
        noteRepository.saveNote(newNote)
        return newNote.id
    }
}
