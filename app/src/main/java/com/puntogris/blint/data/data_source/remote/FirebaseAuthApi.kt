package com.puntogris.blint.data.data_source.remote

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await

class FirebaseAuthApi(private val firebase: FirebaseClients) : AuthServerApi {

    override suspend fun signInWithCredential(credential: AuthCredential): AuthResult {
        return firebase.auth.signInWithCredential(credential).await()
    }

    override fun signOut() {
        firebase.auth.signOut()
    }
}