package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.RepoResult
import com.puntogris.blint.feature_store.domain.model.order.NewOrder
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import com.puntogris.blint.feature_store.domain.model.order.Record
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {

    fun getOrdersPaged(): Flow<PagingData<OrderWithRecords>>

    fun getRecordsPaged(): Flow<PagingData<Record>>

    fun saveOrder(newOrder: NewOrder): Flow<RepoResult<Unit>>

    suspend fun getOrderRecords(orderId: String): OrderWithRecords
}