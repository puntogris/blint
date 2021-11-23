package com.puntogris.blint.data.repository.backup

import com.puntogris.blint.utils.types.BackupState
import kotlinx.coroutines.flow.Flow

interface IBackupRepository {

    fun createBackup(path: String): Flow<BackupState>

    fun restoreBackup(path: String): Flow<BackupState>

    fun checkLastBackUpDate(): Flow<BackupState>
}