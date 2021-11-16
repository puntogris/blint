package com.puntogris.blint.data.data_source.remote

sealed class LoginResult {
    object InProgress : LoginResult()
    class Success() : LoginResult()
    object Error : LoginResult()
}

