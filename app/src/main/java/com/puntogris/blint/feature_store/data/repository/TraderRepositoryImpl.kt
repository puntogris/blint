package com.puntogris.blint.feature_store.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.UUIDGenerator
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.common.utils.types.TraderFilter
import com.puntogris.blint.common.utils.types.TraderQuery
import com.puntogris.blint.feature_store.data.data_source.local.dao.RecordsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.StoreDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.TradersDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.repository.TraderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TraderRepositoryImpl(
    private val tradersDao: TradersDao,
    private val usersDao: UsersDao,
    private val recordsDao: RecordsDao,
    private val storeDao: StoreDao,
    private val dispatcher: DispatcherProvider
) : TraderRepository {

    override suspend fun saveTrader(trader: Trader): SimpleResource = withContext(dispatcher.io) {
        SimpleResource.build {
            if (trader.traderId.isBlank()) {
                with(trader) {
                    traderId = UUIDGenerator.randomUUID()
                    storeId = usersDao.getCurrentStoreId()
                }
                storeDao.incrementTotalTraders()
            }
            tradersDao.insert(trader)
        }
    }

    override fun getTradersPaged(query: TraderQuery?): Flow<PagingData<Trader>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            when {
                query == null -> tradersDao.getTradersSearchPaged("")
                query.filter == TraderFilter.All -> tradersDao.getTradersSearchPaged(query.query)
                else -> tradersDao.getTradersSearchPaged(query.query, query.filter.type)
            }
        }.flow
    }

    override suspend fun deleteTrader(traderId: String): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                tradersDao.delete(traderId)
                storeDao.decrementTotalTraders()
            }
        }

    override suspend fun getTrader(traderId: String): Trader = withContext(dispatcher.io) {
        tradersDao.getTrader(traderId)
    }

    override suspend fun getTrader() = tradersDao.getTraders()

    override fun getTraderFlow(traderId: String) = tradersDao.getTraderFlow(traderId)

    override fun getTradersRecordsPaged(traderId: String): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { recordsDao.getTradersRecords(traderId) }.flow
    }
}
