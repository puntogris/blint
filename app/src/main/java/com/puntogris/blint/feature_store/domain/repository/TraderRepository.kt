package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.model.order.Record
import kotlinx.coroutines.flow.Flow

interface TraderRepository {

    suspend fun saveTrader(trader: Trader): SimpleResource

    suspend fun deleteTrader(traderId: String): SimpleResource

    suspend fun getTrader(traderId: String): Trader

    suspend fun getTrader(): List<Trader>

    fun getTradersPaged(query: String? = null): Flow<PagingData<Trader>>

    fun getTradersRecordsPaged(traderId: String): Flow<PagingData<Record>>
}