package com.puntogris.blint.feature_store.domain.repository

import androidx.activity.result.ActivityResult
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.data.data_source.remote.LoginResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signOutUser(): SimpleResult

    fun serverAuthWithGoogle(result: ActivityResult): Flow<LoginResult>
}