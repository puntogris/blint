package com.puntogris.blint.data.repository.orders

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.puntogris.blint.R
import com.puntogris.blint.data.data_source.local.AppDatabase
import com.puntogris.blint.data.data_source.local.dao.*
import com.puntogris.blint.data.data_source.toOrderWithRecords
import com.puntogris.blint.model.order.NewOrder
import com.puntogris.blint.model.order.OrderRecordCrossRef
import com.puntogris.blint.model.order.OrderWithRecords
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.RepoResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.absoluteValue

class OrderRepository @Inject constructor(
    private val debtsDao: DebtsDao,
    private val clientsDao: ClientsDao,
    private val suppliersDao: SuppliersDao,
    private val statisticsDao: StatisticsDao,
    private val businessDao: BusinessDao,
    private val ordersDao: OrdersDao,
    private val productsDao: ProductsDao,
    private val dispatcher: DispatcherProvider,
    private val appDatabase: AppDatabase
) : IOrdersRepository {

    override fun saveOrder(newOrder: NewOrder): Flow<RepoResult<Unit>> = flow {
        try {
            emit(RepoResult.InProgress)

            val business = businessDao.getCurrentBusiness()
            val statistics = statisticsDao.getStatistics()

            val orderWithRecords = newOrder.toOrderWithRecords(business, statistics)

            orderWithRecords.debt?.let {
                if (it.traderType == Constants.CLIENT) {
                    statisticsDao.updateClientsDebt(it.amount)
                    clientsDao.updateClientDebt(it.traderId, it.amount)
                } else {
                    statisticsDao.updateSupplierDebt(it.amount)
                    suppliersDao.updateSupplierDebt(it.traderId, it.amount)
                }
                debtsDao.insert(it)
            }

            orderWithRecords.records.forEach {
                productsDao.updateProductAmountWithType(
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
                statisticsDao.incrementTotalOrders()
                ordersDao.insertOrder(orderWithRecords)
                ordersDao.insertOrderRecordsCrossRef(recordRefs)
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
            ordersDao.getAllOrdersPaged()
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
            ordersDao.getAllRecordsPaged()
        }.flow
    }

    override suspend fun getOrderRecords(orderId: String): OrderWithRecords =
        withContext(dispatcher.io) {
            ordersDao.getAllOrderRecords(orderId)
        }

}