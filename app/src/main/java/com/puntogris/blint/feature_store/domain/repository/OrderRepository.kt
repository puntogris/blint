package com.puntogris.blint.feature_store.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.feature_store.domain.model.order.NewOrder
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import com.puntogris.blint.feature_store.domain.model.order.Record
import kotlinx.coroutines.flow.Flow
import java.io.File

interface OrderRepository {

    fun generateOrderPDF(uri: Uri?, orderWithRecords: OrderWithRecords): Resource<File>

    fun getOrdersPaged(): Flow<PagingData<OrderWithRecords>>

    fun getRecordsPaged(): Flow<PagingData<Record>>

    fun saveOrder(newOrder: NewOrder): Flow<ProgressResource<Unit>>

    suspend fun getOrderRecords(orderId: String): OrderWithRecords
}
