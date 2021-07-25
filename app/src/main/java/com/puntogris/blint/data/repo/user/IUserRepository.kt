package com.puntogris.blint.data.repo.user

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.*
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    fun checkIfUserIsLogged():Boolean
    suspend fun sendReportToFirestore(message: String): SimpleResult
    fun getEmployeeBusiness(): StateFlow<UserBusiness>
    fun getCurrentUID() :String
    fun getCurrentUser() : FirebaseUser?
    fun getOwnerBusiness(): StateFlow<RepoResult<List<Employee>>>
    fun getBusinessEmployees(businessId:String):StateFlow<UserBusiness>
    suspend fun generateJoiningCode(businessId: String): RepoResult<JoinCode>
    suspend fun syncAccountFromDatabase(userData: UserData? = null): SyncAccount
    suspend fun getUserBusiness():List<Employee>
}