package com.puntogris.blint.data.repository.login

import androidx.activity.result.ActivityResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.data.data_source.remote.AuthServerApi
import com.puntogris.blint.data.data_source.remote.GoogleSingInApi
import com.puntogris.blint.data.data_source.remote.LoginResult
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.User
import com.puntogris.blint.model.UserData
import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.types.RegistrationData
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val businessDao: BusinessDao,
    private val usersDao: UsersDao,
    private val sharedPreferences: SharedPreferences,
    private val authServerApi: AuthServerApi,
    private val googleSingInApi: GoogleSingInApi
) : ILoginRepository {

    val auth = FirebaseAuth.getInstance()
    val firestore = Firebase.firestore

    override fun serverAuthWithGoogle(result: ActivityResult): Flow<LoginResult> = flow {
        try {
            emit(LoginResult.InProgress)
            val credential = googleSingInApi.getCredentialWithIntent(requireNotNull(result.data))
            val authResult = authServerApi.signInWithCredential(credential)
            //todo return user to sync maybe
            emit(LoginResult.Success())
        } catch (e: Exception) {
            googleSingInApi.signOut()
            emit(LoginResult.Error)
        }
    }

    override suspend fun singInAnonymously() = withContext(Dispatchers.IO) {
        try {
            val business = Business()
            businessDao.insert(business)
            usersDao.insert(User(currentBusinessId = business.businessId))
            sharedPreferences.setShowLoginScreen(false)
            SimpleResult.Success
        } catch (e: Exception) {
            SimpleResult.Failure
        }
    }

    override suspend fun signOutUser() = withContext(Dispatchers.IO) {
        try {
            googleSingInApi.signOut()
            authServerApi.signOut()

            businessDao.deleteAll()
            sharedPreferences.setShowNewUserScreenPref(true)
            sharedPreferences.setShowLoginScreen(false)
            auth.signOut()
        } catch (e: Exception) {
            //handle
        }
    }

    override suspend fun checkUserDataInFirestore(user: FirestoreUser): RegistrationData =
        withContext(Dispatchers.IO) {
            try {
                val document =
                    firestore.collection(USERS_COLLECTION).document(user.uid).get().await()
                val userdata = document.toObject(UserData::class.java) ?: UserData()
                val roomUser = User(currentUid = user.uid)
                usersDao.insert(roomUser)

                if (!document.exists()) {
                    firestore.collection(USERS_COLLECTION).document(user.uid).set(user).await()
                    RegistrationData.NotFound
                } else if (userdata.dataMissing()) RegistrationData.Incomplete
                else RegistrationData.Complete(userdata)

            } catch (e: Exception) {
                RegistrationData.Error
            }
        }
}