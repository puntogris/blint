package com.puntogris.blint.feature_store.data.repository

import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.feature_store.data.data_source.local.dao.TrafficDao
import com.puntogris.blint.feature_store.domain.model.Traffic
import com.puntogris.blint.feature_store.domain.repository.TrafficRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TrafficRepositoryImp(
    private val trafficDao: TrafficDao,
    private val dispatchers: DispatcherProvider
) : TrafficRepository {

    override suspend fun getLastTraffic(days: Int) = withContext(dispatchers.io) {
        trafficDao.getTraffic(days)
    }

    override fun getLastTrafficFlow(days: Int): Flow<List<Traffic>> {
        return trafficDao.getTrafficFlow(days)
    }
}
