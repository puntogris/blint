package com.puntogris.blint.data.repository.login

import androidx.activity.result.ActivityResult
import com.puntogris.blint.data.data_source.remote.LoginResult
import kotlinx.coroutines.flow.Flow

interface ILoginRepository {
    suspend fun signOutUser()

    fun serverAuthWithGoogle(result: ActivityResult): Flow<LoginResult>
}