package com.puntogris.blint.data.repository.user

import com.puntogris.blint.model.AuthUser
import com.puntogris.blint.model.User
import com.puntogris.blint.utils.types.SyncAccount

interface IUserRepository {

    suspend fun syncUserAccount(authUser: AuthUser? = null): SyncAccount

    suspend fun getCurrentUser(): User
}