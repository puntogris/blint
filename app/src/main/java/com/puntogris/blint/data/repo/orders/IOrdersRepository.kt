package com.puntogris.blint.data.repo.orders

import androidx.paging.PagingData
import com.puntogris.blint.model.OrderWithRecords
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IOrdersRepository {
    suspend fun getBusinessOrdersPagingDataFlow(): Flow<PagingData<OrderWithRecords>>
    suspend fun getBusinessRecordsPagingDataFlow(): Flow<PagingData<Record>>
    suspend fun saveOrderIntoDatabase(order: OrderWithRecords): SimpleResult
    suspend fun getOrderRecords(orderId:String): OrderWithRecords
}