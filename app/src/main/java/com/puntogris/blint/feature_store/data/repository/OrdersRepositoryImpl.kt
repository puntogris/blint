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
import com.puntogris.blint.common.utils.types.RepoResult
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.feature_store.data.data_source.local.AppDatabase
import com.puntogris.blint.feature_store.data.data_source.toOrderWithRecords
import com.puntogris.blint.feature_store.domain.model.order.NewOrder
import com.puntogris.blint.feature_store.domain.model.order.OrderRecordCrossRef
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.absoluteValue

class OrdersRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val appDatabase: AppDatabase,
    private val pdfCreator: PDFCreator
) : OrdersRepository {

    override fun saveOrder(newOrder: NewOrder): Flow<RepoResult<Unit>> = flow {
        try {
            emit(RepoResult.InProgress)

            val business = appDatabase.businessDao.getCurrentBusiness()

            val orderWithRecords = newOrder.toOrderWithRecords(business)

            orderWithRecords.debt?.let {
                if (it.traderType == Constants.CLIENT) {
                    appDatabase.businessDao.updateClientsDebt(it.amount)
                    appDatabase.clientsDao.updateClientDebt(it.traderId, it.amount)
                } else {
                    appDatabase.businessDao.updateSupplierDebt(it.amount)
                    appDatabase.suppliersDao.updateSupplierDebt(it.traderId, it.amount)
                }
                appDatabase.debtsDao.insert(it)
            }

            orderWithRecords.records.forEach {
                appDatabase.productsDao.updateProductAmountWithType(
                    it.productId, it.amount.absoluteValue,
                    orderWithRecords.order.type
                )
            }

            val recordRefs = orderWithRecords.records.map {
                OrderRecordCrossRef(
                    orderWithRecords.order.orderId,
                    it.recordId
                )
            }

            appDatabase.withTransaction {
                appDatabase.businessDao.incrementTotalOrders()
                appDatabase.ordersDao.insert(orderWithRecords.order)
                appDatabase.recordsDao.insert(orderWithRecords.records)
                appDatabase.ordersDao.insertOrderRecordsCrossRef(recordRefs)
            }

            emit(RepoResult.Success(Unit))
        } catch (e: Exception) {
            emit(RepoResult.Error(R.string.snack_order_created_error))
        }
    }.flowOn(dispatcher.io)

    override fun getOrdersPaged(): Flow<PagingData<OrderWithRecords>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            appDatabase.ordersDao.getAllOrdersPaged()
        }.flow
    }


    override fun getRecordsPaged(): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            appDatabase.recordsDao.getAllRecordsPaged()
        }.flow
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
