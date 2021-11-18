package com.puntogris.blint.data.repository.orders

import androidx.paging.PagingData
import com.puntogris.blint.model.OrderWithRecords
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IOrdersRepository {

    suspend fun getBusinessOrdersPaged(): Flow<PagingData<OrderWithRecords>>

    suspend fun getBusinessRecordsPaged(): Flow<PagingData<Record>>

    suspend fun saveOrder(order: OrderWithRecords): SimpleResult

    suspend fun getOrderRecords(orderId: String): OrderWithRecords
}