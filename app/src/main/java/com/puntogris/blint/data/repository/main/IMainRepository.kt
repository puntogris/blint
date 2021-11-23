package com.puntogris.blint.data.repository.main

import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Event
import com.puntogris.blint.model.Statistic
import kotlinx.coroutines.flow.Flow

interface IMainRepository {

    fun getBusinessLastEventsDatabase(): Flow<List<Event>>

    fun getBusinessCounterFlow(): Flow<Statistic>

    suspend fun getBusinessListRoom(): List<Business>
}