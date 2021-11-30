package com.puntogris.blint.feature_auth.data.repository

import androidx.activity.result.ActivityResult
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_auth.data.data_source.GoogleSingInApi
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.data.data_source.remote.AuthServerApi
import com.puntogris.blint.feature_store.data.data_source.remote.LoginResult
import com.puntogris.blint.feature_store.data.data_source.toAuthUser
import com.puntogris.blint.feature_store.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val authServerApi: AuthServerApi,
    private val googleSingInApi: GoogleSingInApi,
    private val dispatcher: DispatcherProvider
) : AuthRepository {

    override fun serverAuthWithGoogle(result: ActivityResult): Flow<LoginResult> = flow {
        try {
            emit(LoginResult.InProgress)
            val credential = googleSingInApi.getCredentialWithIntent(requireNotNull(result.data))
            val authResult = authServerApi.signInWithCredential(credential)
            val authUser = requireNotNull(authResult.user).toAuthUser()
            emit(LoginResult.Success(authUser))
        } catch (e: Exception) {
            googleSingInApi.signOut()
            emit(LoginResult.Error)
        }
    }

    override suspend fun signOutUser() = withContext(dispatcher.io) {
        SimpleResult.build {
            googleSingInApi.signOut()
            authServerApi.signOut()
            sharedPreferences.setShowNewUserScreen(true)
            sharedPreferences.setShowLoginScreen(true)
        }
    }
}