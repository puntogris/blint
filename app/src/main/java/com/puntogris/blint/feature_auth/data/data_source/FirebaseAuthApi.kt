package com.puntogris.blint.feature_auth.data.data_source

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.puntogris.blint.common.data.data_source.FirebaseClients
import com.puntogris.blint.feature_store.data.data_source.remote.AuthServerApi
import kotlinx.coroutines.tasks.await

class FirebaseAuthApi(private val firebase: FirebaseClients) : AuthServerApi {

    override suspend fun signInWithCredential(credential: AuthCredential): AuthResult {
        return firebase.auth.signInWithCredential(credential).await()
    }

    override fun signOut() {
        firebase.auth.signOut()
    }
}