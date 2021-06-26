package com.puntogris.blint.data.remote

import com.puntogris.blint.model.Business
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.StateFlow

interface IBackupRepository {
    suspend fun createBackupForBusiness(path: String): SimpleResult
    suspend fun restoreBackupForBusiness(path: String): SimpleResult
    fun checkBackUpRequirements(): StateFlow<RepoResult<List<Business>>>
    fun checkLastBackUpDate(): StateFlow<RepoResult<Long>>
}