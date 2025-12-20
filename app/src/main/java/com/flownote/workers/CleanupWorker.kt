package com.flownote.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flownote.data.repository.NoteRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker to clean up expired temporary notes
 * Runs periodically (daily) to delete notes that have passed their deleteAfter date
 */
@HiltWorker
class CleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val noteRepository: NoteRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Delete all expired temporary notes
            noteRepository.deleteExpiredNotes()
            
            Result.success()
        } catch (e: Exception) {
            // Log error and retry
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "CleanupExpiredNotesWork"
    }
}
