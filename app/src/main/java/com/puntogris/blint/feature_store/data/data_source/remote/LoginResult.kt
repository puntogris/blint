package com.puntogris.blint.feature_store.data.data_source.remote

import com.puntogris.blint.feature_store.domain.model.AuthUser

sealed class LoginResult {
    object InProgress : LoginResult()
    class Success(val authUser: AuthUser) : LoginResult()
    object Error : LoginResult()
}

