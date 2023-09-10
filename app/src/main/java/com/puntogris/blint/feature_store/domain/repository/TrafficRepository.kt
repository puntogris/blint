package com.puntogris.blint.feature_store.domain.repository

import com.puntogris.blint.feature_store.domain.model.Traffic
import kotlinx.coroutines.flow.Flow

interface TrafficRepository {

    suspend fun getLastTraffic(days: Int): List<Traffic>

    fun getLastTrafficFlow(days: Int): Flow<List<Traffic>>

}
