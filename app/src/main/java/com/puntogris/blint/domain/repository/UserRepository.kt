package com.puntogris.blint.domain.repository

import com.puntogris.blint.model.AuthUser
import com.puntogris.blint.model.Ticket
import com.puntogris.blint.model.User
import com.puntogris.blint.utils.types.BackupState
import com.puntogris.blint.utils.types.SimpleRepoResult
import com.puntogris.blint.utils.types.SyncAccount
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun syncUserAccount(authUser: AuthUser? = null): SyncAccount

    fun getUserFlow(): Flow<User>

    fun sendTicket(ticket: Ticket): Flow<SimpleRepoResult>

    fun createBackup(path: String): Flow<BackupState>

    fun restoreBackup(path: String): Flow<BackupState>

    fun checkLastBackUpDate(): Flow<BackupState>
}