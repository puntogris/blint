package com.puntogris.blint.data.data_source.remote

import com.puntogris.blint.model.AuthUser
import com.puntogris.blint.model.User

interface UserServerApi {
    suspend fun getUserAccount(authUser: AuthUser): User
}