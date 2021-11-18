package com.puntogris.blint.data.repository.login

import androidx.activity.result.ActivityResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.data.data_source.remote.AuthServerApi
import com.puntogris.blint.data.data_source.remote.GoogleSingInApi
import com.puntogris.blint.data.data_source.remote.LoginResult
import com.puntogris.blint.data.data_source.toAuthUser
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.User
import com.puntogris.blint.model.AuthUser
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.RegistrationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val businessDao: BusinessDao,
    private val sharedPreferences: SharedPreferences,
    private val authServerApi: AuthServerApi,
    private val googleSingInApi: GoogleSingInApi,
    private val dispatcher: DispatcherProvider
) : ILoginRepository {

    val auth = FirebaseAuth.getInstance()
    val firestore = Firebase.firestore

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
            auth.signOut()
        } catch (e: Exception) {
            //handle
        }
    }
}