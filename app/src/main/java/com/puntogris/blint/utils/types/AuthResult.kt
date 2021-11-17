package com.puntogris.blint.utils.types

import com.google.firebase.auth.FirebaseUser

sealed class AuthResult {
    class Success(val user: FirebaseUser) : AuthResult()
    class Error(val exception: Exception) : AuthResult()
    object InProgress : AuthResult()
}