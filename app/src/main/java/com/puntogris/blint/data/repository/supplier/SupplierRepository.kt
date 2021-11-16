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
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SupplierRepository @Inject constructor(
    private val suppliersDao: SuppliersDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao
) : ISupplierRepository {

    private suspend fun currentBusiness() = usersDao.getCurrentBusinessFromUser()

    override suspend fun saveSupplierDatabase(supplier: Supplier): SimpleResult =
        withContext(Dispatchers.IO) {
            try {
                val isNewSupplier = supplier.supplierId.isEmpty()
                val user = currentBusiness()


                if (isNewSupplier) {
                    supplier.apply {
                        businessId = user.businessId
                        supplierId = "" //todo generate id automatically
                    }
                }

                suppliersDao.insert(supplier)
                if (isNewSupplier) statisticsDao.incrementTotalSuppliers()

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun getSupplierPagingDataFlow(): Flow<PagingData<Supplier>> =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                suppliersDao.getAllPaged()
            }.flow
        }

    override suspend fun deleteSupplierDatabase(supplierId: String): SimpleResult =
        withContext(Dispatchers.IO) {
            try {
                suppliersDao.delete(supplierId)
                statisticsDao.decrementTotalSuppliers()

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun getSupplierRecordsPagingDataFlow(supplierId: String): Flow<PagingData<Record>> =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                ordersDao.getSupplierRecords(supplierId)
            }.flow
        }

    override suspend fun getSupplierWithNamePagingDataFlow(name: String): Flow<PagingData<Supplier>> =
        withContext(Dispatchers.IO) {
            Pager(
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