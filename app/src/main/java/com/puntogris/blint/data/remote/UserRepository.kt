package com.puntogris.blint.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.user.UsersDao
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.Constants.BUG_REPORT_COLLECTION_NAME
import com.puntogris.blint.utils.Constants.REPORT_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.TIMESTAMP_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class UserRepository @Inject constructor(private val usersDao: UsersDao) : IUserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override fun singOutCurrentUser() {
        auth.signOut()
    }

    private fun getCurrentUID() = auth.currentUser?.uid.toString()

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

    override suspend fun saveUserToDatabases(user: FirestoreUser){
        firestore.collection(USERS_COLLECTION).document(user.uid).get().addOnSuccessListener {
            if (!it.exists()){
                firestore.collection(USERS_COLLECTION).document(user.uid).set(user)
            }
            else{
                //we populate the room databse with the data
            }
        }
    }

    override suspend fun sendReportToFirestore(message: String): RepoResult {
        val report = hashMapOf(
            REPORT_FIELD_FIRESTORE to message,
            TIMESTAMP_FIELD_FIRESTORE to Timestamp.now()
        )
        return try {
            firestore.collection(BUG_REPORT_COLLECTION_NAME).document().set(report).await()
            RepoResult.Success
        }catch (e: Exception){
            RepoResult.Failure
        }
    }

    override fun saveBusinessTOFirestore(business: Business) {
        val docRef = firestore.collection("business").document()
        docRef.set(business)
    }


//
//    override suspend fun getCurrentUserFromDatabase() = userDao.getUser(auth.uid.toString())

//
//    override suspend fun editUserPhoneNumber(phoneNumber: String) {
//        userDao.updateUserPhoneNumber(getCurrentUID(), phoneNumber)
//        firestore.collection(USERS_COLLECTION).document(getCurrentUID()).update("phoneNumber", phoneNumber)
//    }

}