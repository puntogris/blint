package com.puntogris.blint.data.repo.imp

import androidx.paging.PagingData
import com.puntogris.blint.model.Order
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IOrdersRepository {
    suspend fun getBusinessOrdersPagingDataFlow(): Flow<PagingData<Order>>
    suspend fun getBusinessRecordsPagingDataFlow(): Flow<PagingData<Record>>
    suspend fun saveOrderIntoDatabase(order: Order): SimpleResult
}