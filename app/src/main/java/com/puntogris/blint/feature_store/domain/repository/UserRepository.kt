package com.puntogris.blint.feature_store.domain.repository

import com.puntogris.blint.common.utils.types.BackupState
import com.puntogris.blint.common.utils.types.SimpleRepoResult
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.common.utils.types.SyncAccount
import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.Ticket
import com.puntogris.blint.feature_store.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun syncUserAccount(authUser: AuthUser): SyncAccount

    suspend fun updateCurrentBusiness(businessId: String): SimpleResult

    fun getUserFlow(): Flow<User>

    fun sendTicket(ticket: Ticket): Flow<SimpleRepoResult>

    fun createBackup(path: String): Flow<BackupState>

    fun restoreBackup(path: String): Flow<BackupState>

    fun checkLastBackupTimestamp(): Flow<BackupState>
}