package com.flownote.ui.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flownote.data.repository.BackupRepository
import com.flownote.data.repository.SettingsRepository
import com.flownote.data.repository.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    val themeMode = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val useDynamicColors = repository.useDynamicColors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Backup state
    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState: StateFlow<BackupState> = _backupState.asStateFlow()

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDynamicColors(enabled)
        }
    }

    /**
     * Export notes to ZIP file
     */
    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            val result = backupRepository.exportNotesToZip(uri)
            _backupState.value = if (result.isSuccess) {
                BackupState.ExportSuccess
            } else {
                BackupState.Error(result.exceptionOrNull()?.message ?: "Export failed")
            }
        }
    }

    /**
     * Import notes from ZIP file
     */
    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            val result = backupRepository.importNotesFromZip(uri)
            _backupState.value = if (result.isSuccess) {
                BackupState.ImportSuccess(result.getOrNull() ?: 0)
            } else {
                BackupState.Error(result.exceptionOrNull()?.message ?: "Import failed")
            }
        }
    }

    /**
     * Clear all app data
     */
    fun clearAllData() {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            val result = backupRepository.clearAllNotes()
            _backupState.value = if (result.isSuccess) {
                BackupState.ClearSuccess
            } else {
                BackupState.Error(result.exceptionOrNull()?.message ?: "Clear data failed")
            }
        }
    }

    /**
     * Reset backup state
     */
    fun resetBackupState() {
        _backupState.value = BackupState.Idle
    }
}

/**
 * State for backup operations
 */
sealed class BackupState {
    object Idle : BackupState()
    object Loading : BackupState()
    object ExportSuccess : BackupState()
    data class ImportSuccess(val noteCount: Int) : BackupState()
    object ClearSuccess : BackupState()
    data class Error(val message: String) : BackupState()
}
