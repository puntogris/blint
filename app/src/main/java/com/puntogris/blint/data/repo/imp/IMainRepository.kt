package com.puntogris.blint.data.repo.imp

import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.utils.EventsDashboard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IMainRepository {
    suspend fun getBusinessLastEventsDatabase(): EventsDashboard
    fun getAllUnreadNotifications(): StateFlow<Int>
    suspend fun getBusinessCounterFlow(): Flow<BusinessCounters>
    fun checkIfUserIsLogged():Boolean
}