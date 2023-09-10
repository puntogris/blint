package com.puntogris.blint.feature_store.data.data_source.remote

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult

interface AuthServerApi {
    suspend fun signInWithCredential(credential: AuthCredential): AuthResult

    fun signOut()
}
