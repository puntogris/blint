package com.puntogris.blint.data.repo.irepo

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.*
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    fun checkIfUserIsLogged():Boolean
    fun singOutCurrentUser()
    suspend fun sendReportToFirestore(message: String): SimpleResult
    fun getEmployeeBusiness(): StateFlow<UserBusiness>
    fun getCurrentUID() :String
    fun getCurrentUser() : FirebaseUser?
    fun getOwnerBusiness(): StateFlow<RepoResult<List<Employee>>>
    fun getBusinessEmployees(businessId:String):StateFlow<UserBusiness>
    suspend fun generateJoiningCode(businessId: String): RepoResult<JoinCode>
    suspend fun createEmployeeWithCode(code:String): JoinBusiness
    suspend fun syncAccountFromDatabase(userData: UserData? = null): SyncAccount
}