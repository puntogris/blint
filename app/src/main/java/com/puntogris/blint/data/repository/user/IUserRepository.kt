package com.puntogris.blint.data.repository.user

import com.puntogris.blint.model.AuthUser
import com.puntogris.blint.model.User
import com.puntogris.blint.utils.types.SyncAccount
import kotlinx.coroutines.flow.Flow

interface IUserRepository {

    suspend fun syncUserAccount(authUser: AuthUser? = null): SyncAccount

    fun getUserFlow(): Flow<User>
}