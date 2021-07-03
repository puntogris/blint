package com.puntogris.blint.data.repo.imp

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.EmployeeRequest
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.JoinCode
import com.puntogris.blint.utils.*
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    fun checkIfUserIsLogged():Boolean
    fun singOutCurrentUser()
    fun logInUserWithCredentialToken(credentialToken: String):StateFlow<AuthResult>
    suspend fun checkUserDataInFirestore(user: FirestoreUser): RegistrationData
    suspend fun sendReportToFirestore(message: String): SimpleResult
    fun getEmployeeBusiness(): StateFlow<UserBusiness>
    fun getCurrentUID() :String
    suspend fun registerNewBusiness(name:String):RepoResult<String>
    fun getCurrentUser() : FirebaseUser?
    fun getOwnerBusiness(): StateFlow<RepoResult<List<Employee>>>
    fun getBusinessEmployees(businessId:String):StateFlow<UserBusiness>
    suspend fun updateUserNameCountry(username:String, country:String):SimpleResult
    fun sendEmployeeRequest(request: EmployeeRequest): StateFlow<RequestResult>
    suspend fun generateJoiningCode(businessId: String): RepoResult<JoinCode>
    suspend fun createEmployeeWithCode(code:String):JoinBusiness
}