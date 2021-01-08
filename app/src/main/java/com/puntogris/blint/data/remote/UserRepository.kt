package com.puntogris.blint.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.user.UsersDao
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(private val usersDao: UsersDao) : IUserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override fun singOutCurrentUser() {
        auth.signOut()
    }

    private fun getCurrentUID() = auth.currentUser?.uid.toString()

    override fun logInUserWithCredentialToken(credentialToken: String):StateFlow<AuthResult> {
        val result = MutableStateFlow<AuthResult>(AuthResult.InProgress)
        val authCredential = GoogleAuthProvider.getCredential(credentialToken, null)
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = AuthResult.Success(task.result.user!!)
                } else {
                    task.exception?.let {
                        result.value = AuthResult.Error(it.message.toString())
                    }
                }
            }
        return result
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
//
//    override suspend fun getCurrentUserFromDatabase() = userDao.getUser(auth.uid.toString())
//
//    override fun sendUserSuggestion(message: String) {
//        val suggestion = UserSuggestion(auth.currentUser?.uid.toString(), message)
//        firestore.collection(USERS_SUGGESTIONS_COLLECTION).document().set(suggestion)
//    }
//
//    override suspend fun editUserPhoneNumber(phoneNumber: String) {
//        userDao.updateUserPhoneNumber(getCurrentUID(), phoneNumber)
//        firestore.collection(USERS_COLLECTION).document(getCurrentUID()).update("phoneNumber", phoneNumber)
//    }

}