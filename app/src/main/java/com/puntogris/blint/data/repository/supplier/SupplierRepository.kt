package com.puntogris.blint.data.repository.supplier

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.data_source.local.dao.OrdersDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.SuppliersDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SupplierRepository @Inject constructor(
    private val suppliersDao: SuppliersDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
    private val dispatcher: DispatcherProvider
) : ISupplierRepository {

    override suspend fun saveSupplierDatabase(supplier: Supplier): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                if (supplier.supplierId == 0) {
                    supplier.businessId = usersDao.getCurrentBusinessId()
                    statisticsDao.incrementTotalSuppliers()
                }

                suppliersDao.insert(supplier)
            }
        }

    override fun getAllSuppliersPaged(): Flow<PagingData<Supplier>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            suppliersDao.getAllPaged()
        }.flow
    }

    override suspend fun deleteSupplierDatabase(supplierId: Int): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                suppliersDao.delete(supplierId)
                statisticsDao.decrementTotalSuppliers()
            }
        }

    override fun getAllSuppliersRecordsPaged(supplierId: Int): Flow<PagingData<Record>> {
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

    override fun getSuppliersWithNamePaged(name: String): Flow<PagingData<Supplier>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            suppliersDao.getPagedSearch("%${name}%")
        }.flow
    }
}