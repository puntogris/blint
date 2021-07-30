package com.puntogris.blint.data.repo.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.User
import com.puntogris.blint.model.UserData
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.RegistrationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val usersDao: UsersDao,
    private val sharedPref: SharedPref
    ):ILoginRepository {

    val auth = FirebaseAuth.getInstance()
    val firestore = Firebase.firestore

    override fun logInUserWithCredentialToken(credentialToken: String) =
        MutableStateFlow<AuthResult>(AuthResult.InProgress).also {
            val authCredential = GoogleAuthProvider.getCredential(credentialToken, null)
            auth.signInWithCredential(authCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.value = AuthResult.Success(task.result.user!!)
                    } else {
                        task.exception?.let {e ->
                            it.value = AuthResult.Error(e)
                        }
                    }
                }
        }

    override suspend fun signOutUser() = withContext(Dispatchers.IO){
        employeeDao.deleteAll()
        auth.signOut()
        sharedPref.setShowNewUserScreenPref(true)
        sharedPref.setLoginCompletedPref(false)
    }


    override suspend fun checkUserDataInFirestore(user: FirestoreUser): RegistrationData = withContext(Dispatchers.IO){
        try {
            val document = firestore.collection(USERS_COLLECTION).document(user.uid).get().await()
            val userdata = document.toObject(UserData::class.java) ?: UserData()
            val roomUser = User(currentUid = user.uid)
            usersDao.insert(roomUser)

            if (!document.exists()) {
                firestore.collection(USERS_COLLECTION).document(user.uid).set(user).await()
                RegistrationData.NotFound
            }
            else if (userdata.dataMissing()) RegistrationData.Incomplete
            else RegistrationData.Complete(userdata)

        }
        catch (e:Exception){ RegistrationData.Error }
    }
}