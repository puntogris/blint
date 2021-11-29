package com.puntogris.blint.feature_store.data.repository

import android.net.Uri
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.data.data_source.ExcelDrawer
import com.puntogris.blint.feature_store.data.data_source.local.dao.ClientsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.ProductsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.RecordsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.SuppliersDao
import com.puntogris.blint.feature_store.data.data_source.toProductRecordExcel
import com.puntogris.blint.feature_store.domain.repository.StatisticRepository
import kotlinx.coroutines.withContext

class StatisticRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val excelDrawer: ExcelDrawer,
    private val clientsDao: ClientsDao,
    private val suppliersDao: SuppliersDao,
    private val recordsDao: RecordsDao,
    private val productsDao: ProductsDao
) : StatisticRepository {

    override suspend fun saveClientListExcel(uri: Uri): SimpleResult = withContext(dispatcher.io) {
        SimpleResult.build {
            val clients = clientsDao.getClients()
            excelDrawer.drawClientList(clients, uri)
        }
    }

    override suspend fun saveSupplierListExcel(uri: Uri): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                val suppliers = suppliersDao.getSuppliers()
                excelDrawer.drawSupplierList(suppliers, uri)
            }
        }

    override suspend fun saveProductListExcel(uri: Uri): SimpleResult = withContext(dispatcher.io) {
        SimpleResult.build {
            val products = productsDao.getProducts()
            excelDrawer.drawProductsList(products, uri)
        }
    }

    override suspend fun saveClientsRecords(days: Int, uri: Uri): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                val records = if (days == 0) recordsDao.getAllClientsRecords()
                else recordsDao.getRecordsClientsWithDays((-days).toString())
                excelDrawer.drawClientsRecords(records, uri)
            }
        }

    override suspend fun saveSuppliersRecords(days: Int, uri: Uri): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                val records = if (days == 0) recordsDao.getAllSuppliersRecords()
                else recordsDao.getRecordsSuppliersWithDays((-days).toString())
                excelDrawer.drawSupplierRecords(records, uri)
            }
        }

    override suspend fun saveProductsRecords(days: Int, uri: Uri) = withContext(dispatcher.io) {
        SimpleResult.build {
            val records = productsDao.getProducts().map {

                val record = recordsDao.getRecordsWithDays(it.productId, (-days).toString())
                record.toProductRecordExcel(it)
            }
            excelDrawer.drawProductRecords(records, uri)
        }
    }
}
