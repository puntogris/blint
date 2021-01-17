package com.puntogris.blint.data.remote

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    fun checkIfUserIsLogged():Boolean
    fun singOutCurrentUser()
    fun logInUserWithCredentialToken(credentialToken: String):StateFlow<AuthResult>
    suspend fun checkUserDataInFirestore(user: FirestoreUser): SimpleResult
    suspend fun sendReportToFirestore(message: String): SimpleResult
    fun getEmployeeBusiness(): StateFlow<List<Business>>
    fun getCurrentUID() :String
    suspend fun registerNewBusiness(name:String)
    fun getCurrentUser() : FirebaseUser?
    fun getOwnerBusiness(): StateFlow<RepoResult<List<Business>>>

}