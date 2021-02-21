package com.puntogris.blint.data.remote

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.UserBusiness
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    fun checkIfUserIsLogged():Boolean
    fun singOutCurrentUser()
    fun logInUserWithCredentialToken(credentialToken: String):StateFlow<AuthResult>
    suspend fun checkUserDataInFirestore(user: FirestoreUser): SimpleResult
    suspend fun sendReportToFirestore(message: String): SimpleResult
    fun getEmployeeBusiness(): StateFlow<UserBusiness>
    fun getCurrentUID() :String
    suspend fun registerNewBusiness(name:String)
    fun getCurrentUser() : FirebaseUser?
    fun getOwnerBusiness(): StateFlow<RepoResult<List<Employee>>>
    fun getBusinessEmployees(businessId:String):StateFlow<UserBusiness>
    suspend fun updateUserNameCountry(username:String, country:String):SimpleResult
    suspend fun checkIfUserExistWithEmail(email:String): SimpleResult

}