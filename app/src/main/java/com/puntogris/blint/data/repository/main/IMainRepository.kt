package com.puntogris.blint.data.repository.main

import com.puntogris.blint.model.Business
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.types.AccountStatus
import com.puntogris.blint.utils.types.EventsDashboard
import com.puntogris.blint.utils.types.RepoResult
import kotlinx.coroutines.flow.Flow

interface IMainRepository {
    fun getBusinessLastEventsDatabase(): Flow<List<Event>>

    suspend fun getBusinessCounterFlow(): Flow<BusinessCounters>

    fun checkIfUserIsLogged(): Boolean

    suspend fun updateCurrentBusiness(id: String)

    suspend fun getBusinessListRoom(): List<Business>

    fun getBusinessesStatus(): Flow<RepoResult<List<Business>>>

    suspend fun checkIfAccountIsSynced(business: List<Business>): AccountStatus

    fun getCurrentUserFlow(): Flow<Business>
}