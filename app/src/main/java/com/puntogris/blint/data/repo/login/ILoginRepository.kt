package com.puntogris.blint.data.repo.login

import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.RegistrationData
import kotlinx.coroutines.flow.StateFlow

interface ILoginRepository {
    fun logInUserWithCredentialToken(credentialToken: String):StateFlow<AuthResult>
    suspend fun signOutUser()
    suspend fun checkUserDataInFirestore(user: FirestoreUser): RegistrationData
}