package com.puntogris.blint.data.repository.login

import androidx.activity.result.ActivityResult
import com.puntogris.blint.data.data_source.remote.LoginResult
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.types.RegistrationData
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface ILoginRepository {
    suspend fun signOutUser()

    fun serverAuthWithGoogle(result: ActivityResult): Flow<LoginResult>
}