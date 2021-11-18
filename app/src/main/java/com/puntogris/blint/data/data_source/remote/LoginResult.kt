package com.puntogris.blint.data.data_source.remote

import com.puntogris.blint.model.AuthUser

sealed class LoginResult {
    object InProgress : LoginResult()
    class Success(val authUser: AuthUser) : LoginResult()
    object Error : LoginResult()
}

