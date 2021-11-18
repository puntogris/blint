package com.puntogris.blint.data.repository.login

import androidx.activity.result.ActivityResult
import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.data.data_source.remote.AuthServerApi
import com.puntogris.blint.data.data_source.remote.GoogleSingInApi
import com.puntogris.blint.data.data_source.remote.LoginResult
import com.puntogris.blint.data.data_source.toAuthUser
import com.puntogris.blint.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val authServerApi: AuthServerApi,
    private val googleSingInApi: GoogleSingInApi,
    private val dispatcher: DispatcherProvider
) : ILoginRepository {

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
        try {
            googleSingInApi.signOut()
            authServerApi.signOut()

            sharedPreferences.setShowNewUserScreenPref(true)
            sharedPreferences.setShowLoginScreen(true)
        } catch (e: Exception) {
            //handle
        }
    }
}