package com.puntogris.blint.data.remote

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.BusinessData
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    fun checkIfUserIsLogged():Boolean
    fun singOutCurrentUser()
    fun logInUserWithCredentialToken(credentialToken: String):StateFlow<AuthResult>
    suspend fun checkUserDataInFirestore(user: FirestoreUser): BusinessData
    suspend fun sendReportToFirestore(message: String): RepoResult
    fun saveBusinessTOFirestore(business: Business)
    fun getBusinessForUser(): Flow<List<Employee>>
//    suspend fun getCurrentUserFromDatabase(): RoomUser
//    fun sendUserSuggestion(message: String)
//    suspend fun editUserPhoneNumber(phoneNumber: String)
}