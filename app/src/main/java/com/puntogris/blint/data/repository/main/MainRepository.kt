package com.puntogris.blint.data.repository.main

import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.EventsDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val eventsDao: EventsDao,
    private val statisticsDao: StatisticsDao,
    private val businessDao: BusinessDao,
    private val dispatcher: DispatcherProvider,
) : IMainRepository {

    override suspend fun getBusinessListRoom() = businessDao.getBusiness()

    override fun getBusinessLastEventsDatabase() =
        eventsDao.getLastThreeEventsFlow().flowOn(dispatcher.io)

    override fun getBusinessCounterFlow(): Flow<Statistic> =
        statisticsDao.getBusinessStatisticsFlow().flowOn(dispatcher.io)

}
