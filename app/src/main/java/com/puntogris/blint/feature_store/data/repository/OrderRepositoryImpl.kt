package com.puntogris.blint.feature_store.data.repository

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.puntogris.blint.R
import com.puntogris.blint.common.framework.PDFCreator
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.feature_store.data.data_source.local.AppDatabase
import com.puntogris.blint.feature_store.data.data_source.toOrderWithRecords
import com.puntogris.blint.feature_store.domain.model.Traffic
import com.puntogris.blint.feature_store.domain.model.order.*
import com.puntogris.blint.feature_store.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.absoluteValue

class OrderRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val appDatabase: AppDatabase,
    private val pdfCreator: PDFCreator
) : OrderRepository {

    override fun saveOrder(newOrder: NewOrder): Flow<ProgressResource<Unit>> = flow {
        try {
            emit(ProgressResource.InProgress)

            if (!newOrder.areRecordsValid()) {
                return@flow emit(ProgressResource.Error(R.string.product_amount_empty))
            }

            val store = appDatabase.storeDao.getCurrentStore()
            val orderWithRecords = newOrder.toOrderWithRecords(store)

            val recordRefs = orderWithRecords.records.map {
                OrderRecordCrossRef(orderWithRecords.order.orderId, it.recordId)
            }

            val traffic = Traffic().apply {
                if (orderWithRecords.order.type == Constants.IN) {
                    purchases = orderWithRecords.order.total
                } else {
                    sales = orderWithRecords.order.total
                }
                storeId = store.storeId
            }

            with(appDatabase) {
                withTransaction {

                    orderWithRecords.records.forEach {
                        productsDao.updateProductAmountWithType(
                            it.productId,
                            it.amount.absoluteValue,
                            orderWithRecords.order.type
                        )
                        productsDao.updateProductHistoryStock(
                            id = it.productId,
                            inStock = it.historicInStock,
                            outStock = it.historicOutStock
                        )
                    }

                    orderWithRecords.debt?.let {
                        storeDao.updateTradersDebt(it.amount)
                        tradersDao.updateTraderDebt(it.traderId, it.amount)
                        debtsDao.insert(it)
                    }

                    trafficDao.updateOrInsert(traffic)
                    storeDao.incrementTotalOrders()
                    ordersDao.insert(orderWithRecords.order)
                    ordersDao.insertOrderRecordsCrossRef(recordRefs)
                    recordsDao.insert(orderWithRecords.records)
                }
            }

            emit(ProgressResource.Success(Unit))
        } catch (e: Exception) {
            emit(ProgressResource.Error(R.string.snack_order_created_error))
        }
    }.flowOn(dispatcher.io)

    override fun getOrdersPaged(): Flow<PagingData<OrderWithRecords>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { appDatabase.ordersDao.getAllOrdersPaged() }.flow
    }

    override fun getRecordsPaged(): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { appDatabase.recordsDao.getAllRecordsPaged() }.flow
    }

    override suspend fun getOrderRecords(orderId: String): OrderWithRecords =
        withContext(dispatcher.io) {
            appDatabase.ordersDao.getAllOrderRecords(orderId)
        }

    override fun generateOrderPDF(uri: Uri?, orderWithRecords: OrderWithRecords): Resource<File> {
        return try {
            val file = pdfCreator.createPdf(uri, orderWithRecords)
            Resource.Success(file)
        } catch (e: Exception) {
            Resource.Error(R.string.snack_invoice_save_error)
        }
    }
}
