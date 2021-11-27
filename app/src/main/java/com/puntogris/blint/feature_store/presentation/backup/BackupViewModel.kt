package com.puntogris.blint.feature_store.presentation.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.common.utils.types.BackupState
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val repository: UserRepository
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