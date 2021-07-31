package com.puntogris.blint.data.repo.main

import androidx.lifecycle.LiveData
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.User
import com.puntogris.blint.utils.AccountStatus
import com.puntogris.blint.utils.EventsDashboard
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IMainRepository {
    suspend fun getBusinessLastEventsDatabase(): EventsDashboard
    fun getAllUnreadNotifications(): StateFlow<Int>
    suspend fun getBusinessCounterFlow(): Flow<BusinessCounters>
    fun checkIfUserIsLogged():Boolean
    suspend fun updateCurrentBusiness(id:String)
    suspend fun getBusinessListRoom(): List<Employee>
    fun getBusinessesStatus(): Flow<RepoResult<List<Employee>>>
    suspend fun checkIfAccountIsSynced(employee: List<Employee>):AccountStatus
    fun getCurrentUserFlow(): Flow<Employee>
}