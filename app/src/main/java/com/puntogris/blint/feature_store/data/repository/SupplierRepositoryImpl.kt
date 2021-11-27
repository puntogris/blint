package com.puntogris.blint.feature_store.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.UUIDGenerator
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.data.data_source.local.dao.OrdersDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.SuppliersDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SupplierRepositoryImpl(
    private val suppliersDao: SuppliersDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
    private val dispatcher: DispatcherProvider
) : SupplierRepository {

    override suspend fun saveSupplier(supplier: Supplier): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {

                if (supplier.supplierId.isEmpty()) {
                    supplier.supplierId = UUIDGenerator.randomUUID()
                    supplier.businessId = usersDao.getCurrentBusinessId()
                    statisticsDao.incrementTotalSuppliers()
                }

                suppliersDao.insert(supplier)
            }
        }

    override fun getSuppliersPaged(query: String?): Flow<PagingData<Supplier>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            if (query.isNullOrBlank()) suppliersDao.getSuppliersPaged()
            else suppliersDao.getSuppliersSearchPaged("%${query}%")
        }.flow
    }

    override suspend fun deleteSupplier(supplierId: String): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                suppliersDao.delete(supplierId)
                statisticsDao.decrementTotalSuppliers()
            }
        }

    override fun getSupplierRecordsPaged(supplierId: String): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            ordersDao.getSupplierRecords(supplierId)
        }.flow
    }
}