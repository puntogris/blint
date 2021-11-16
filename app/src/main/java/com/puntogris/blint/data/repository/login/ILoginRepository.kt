package com.puntogris.blint.data.repository.login

import androidx.activity.result.ActivityResult
import com.puntogris.blint.data.data_source.remote.LoginResult
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.RegistrationData
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ILoginRepository {
    suspend fun signOutUser()
    suspend fun checkUserDataInFirestore(user: FirestoreUser): RegistrationData
    fun serverAuthWithGoogle(result: ActivityResult): Flow<LoginResult>
    suspend fun singInAnonymously(): SimpleResult
}