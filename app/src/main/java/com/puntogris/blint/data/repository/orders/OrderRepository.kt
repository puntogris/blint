package com.puntogris.blint.data.repository.orders

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.puntogris.blint.data.data_source.local.dao.OrdersDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.OrderWithRecords
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val ordersDao: OrdersDao,
    private val dispatcher: DispatcherProvider
) : IOrdersRepository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun getBusinessOrdersPagingDataFlow(): Flow<PagingData<OrderWithRecords>> =
        withContext(dispatcher.io) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                ordersDao.getAllOrdersPaged()
            }.flow
        }

    override suspend fun getBusinessRecordsPagingDataFlow(): Flow<PagingData<Record>> =
        withContext(dispatcher.io) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                ordersDao.getAllRecordsPaged()
            }.flow
        }

    override suspend fun saveOrderIntoDatabase(order: OrderWithRecords): SimpleResult =
        withContext(dispatcher.io) {
//            val user = currentBusiness()
            try {
//                order.order.author = auth.currentUser?.email.toString()
//                order.order.businessName = user.name
//
//                if (order.debt != null) {
//                    order.debt?.debtId = order.order.debtId
//                    order.debt?.type = if (order.order.type == IN) SUPPLIER else CLIENT
//                }
//
//                val recordsFinal = order.records.map {
//                    Record(
//                        type = order.order.type,
//                        traderName = order.order.traderName,
//                        traderId = order.order.traderId,
//                        timestamp = order.order.timestamp,
//                        amount = it.amount,
//                        productId = it.productId,
//                        productName = it.productName,
//                        author = order.order.author,
//                        businessId = order.order.businessId,
//                        productUnitPrice = if (it.amount != 0) it.value / it.amount else 0f,
//                        value = it.value,
//                        orderId = order.order.orderId,
//                        totalInStock = it.totalInStock,
//                        totalOutStock = it.totalOutStock,
//                        sku = it.sku,
//                        barcode = it.barcode
//                    ).also { rec ->
//                        if (rec.type == IN) rec.totalInStock += rec.amount.absoluteValue
//                        else rec.totalOutStock += rec.amount.absoluteValue
//                    }
//                }
//
//                ordersDao.insertOrderWithRecords(order, recordsFinal)

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun getOrderRecords(orderId: String): OrderWithRecords =
        withContext(dispatcher.io) {
            ordersDao.getAllOrderRecords(orderId)
        }

}