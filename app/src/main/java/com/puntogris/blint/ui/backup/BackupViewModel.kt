package com.puntogris.blint.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.data.repository.backup.BackupRepository
import com.puntogris.blint.utils.types.BackupState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val repository: BackupRepository
) : ViewModel() {

    val backupState = MutableSharedFlow<BackupState>()

    fun backupBusiness(path: String) {
        viewModelScope.launch {
            backupState.emitAll(repository.createBackup(path))
        }
    }

    fun restoreBackup(path: String) {
        viewModelScope.launch {
            backupState.emitAll(repository.restoreBackup(path))
        }
    }

    init {
        viewModelScope.launch {
            backupState.emitAll(repository.checkLastBackUpDate())
        }
    }

}