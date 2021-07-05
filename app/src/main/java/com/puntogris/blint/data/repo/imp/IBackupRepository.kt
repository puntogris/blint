package com.puntogris.blint.data.repo.imp

import com.puntogris.blint.model.Business
import com.puntogris.blint.utils.BackupState
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.StateFlow

interface IBackupRepository {
    fun createBackupForBusiness(path: String): StateFlow<BackupState>
    fun restoreBackupForBusiness(path: String): StateFlow<BackupState>
    fun checkBackUpRequirements(): StateFlow<RepoResult<List<Business>>>
    fun checkLastBackUpDate(): StateFlow<RepoResult<Long>>
}