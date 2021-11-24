package com.puntogris.blint.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.model.order.NewOrder
import com.puntogris.blint.model.order.OrderWithRecords
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.utils.types.RepoResult
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {

    fun getOrdersPaged(): Flow<PagingData<OrderWithRecords>>

    fun getRecordsPaged(): Flow<PagingData<Record>>

    fun saveOrder(newOrder: NewOrder): Flow<RepoResult<Unit>>

    suspend fun getOrderRecords(orderId: String): OrderWithRecords
}