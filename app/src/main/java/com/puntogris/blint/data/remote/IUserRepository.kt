package com.puntogris.blint.data.remote

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    fun checkIfUserIsLogged():Boolean
    fun singOutCurrentUser()
    fun logInUserWithCredentialToken(credentialToken: String):StateFlow<AuthResult>
    suspend fun saveUserToDatabases(user: FirestoreUser)
    suspend fun sendReportToFirestore(message: String): RepoResult
    fun saveBusinessTOFirestore(business: Business)
//    suspend fun getCurrentUserFromDatabase(): RoomUser
//    fun sendUserSuggestion(message: String)
//    suspend fun editUserPhoneNumber(phoneNumber: String)
}