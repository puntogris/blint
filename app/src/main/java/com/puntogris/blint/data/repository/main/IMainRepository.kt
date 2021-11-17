package com.puntogris.blint.data.repository.main

import com.puntogris.blint.model.Business
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.utils.AccountStatus
import com.puntogris.blint.utils.EventsDashboard
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.flow.Flow

interface IMainRepository {
    suspend fun getBusinessLastEventsDatabase(): EventsDashboard

    suspend fun getBusinessCounterFlow(): Flow<BusinessCounters>

    fun checkIfUserIsLogged():Boolean

    suspend fun updateCurrentBusiness(id: String)

    suspend fun getBusinessListRoom(): List<Business>

    fun getBusinessesStatus(): Flow<RepoResult<List<Business>>>

    suspend fun checkIfAccountIsSynced(business: List<Business>):AccountStatus

    fun getCurrentUserFlow(): Flow<Business>
}