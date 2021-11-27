package com.puntogris.blint.feature_store.data.data_source.remote

import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.User

interface UserServerApi {
    suspend fun getUserAccount(authUser: AuthUser): User
}