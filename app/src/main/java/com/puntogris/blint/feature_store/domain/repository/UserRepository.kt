package com.puntogris.blint.feature_store.domain.repository

import com.puntogris.blint.common.utils.types.BackupState
import com.puntogris.blint.common.utils.types.SimpleProgressResource
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.common.utils.types.SyncAccount
import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.Ticket
import com.puntogris.blint.feature_store.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun syncUserAccount(authUser: AuthUser): SyncAccount

    suspend fun updateCurrentBusiness(businessId: String): SimpleResource

    suspend fun deleteUserAccount(email: String): Flow<SimpleProgressResource>

    fun getUserFlow(): Flow<User>

    suspend fun getUser(): User

    fun sendTicket(ticket: Ticket): Flow<SimpleProgressResource>

    fun createBackup(): Flow<BackupState>

    fun restoreBackup(): Flow<BackupState>

    fun checkLastBackupTimestamp(): Flow<BackupState>
}