package com.puntogris.blint.feature_store.data.repository

import android.net.Uri
import com.puntogris.blint.common.framework.ExcelDrawer
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.data.data_source.local.dao.ClientsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.ProductsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.RecordsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.SuppliersDao
import com.puntogris.blint.feature_store.data.data_source.toProductRecordExcel
import com.puntogris.blint.feature_store.data.data_source.toTraderExcelList
import com.puntogris.blint.feature_store.domain.repository.ReportsRepository
import com.puntogris.blint.feature_store.presentation.reports.ExportReport
import kotlinx.coroutines.withContext

class ReportsRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val excelDrawer: ExcelDrawer,
    private val clientsDao: ClientsDao,
    private val suppliersDao: SuppliersDao,
    private val recordsDao: RecordsDao,
    private val productsDao: ProductsDao
) : ReportsRepository {

    override suspend fun generateClientListReport(report: ExportReport): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                val clients = clientsDao.getClients()
                excelDrawer.drawClientList(clients, requireNotNull(report.uri))
            }
        }

    override suspend fun generateSupplierListReport(report: ExportReport): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                val suppliers = suppliersDao.getSuppliers()
                excelDrawer.drawSupplierList(suppliers, requireNotNull(report.uri))
            }
        }

    override suspend fun generateProductListReport(report: ExportReport): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                val products = productsDao.getProducts()
                excelDrawer.drawProductsList(products, requireNotNull(report.uri))
            }
        }

    override suspend fun generateClientsReport(report: ExportReport): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                val records = when (report.timeFrame.days) {
                    0 -> recordsDao.getClientsRecords()
                    else -> recordsDao.getClientsRecordsTimeframe(report.timeFrame.days)
                }.toTraderExcelList()

                excelDrawer.drawClientsRecords(records, requireNotNull(report.uri))
            }
        }

    override suspend fun generateSuppliersReport(report: ExportReport): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                val records = when (report.timeFrame.days) {
                    0 -> recordsDao.getSuppliersRecords()
                    else -> recordsDao.getSuppliersRecordsTimeframe(report.timeFrame.days)
                }.toTraderExcelList()

                excelDrawer.drawSupplierRecords(records, requireNotNull(report.uri))
            }
        }

    override suspend fun generateProductsReport(report: ExportReport) = withContext(dispatcher.io) {
        SimpleResult.build {
            val products = productsDao.getProducts()

            val records = products.mapNotNull {
                val record = recordsDao.getProductsRecordsTimeFrame(
                    it.productId,
                    report.timeFrame.days
                )
                record?.toProductRecordExcel(it)
            }
            println(records)
            excelDrawer.drawProductRecords(records, report.uri ?: Uri.parse(""))
        }
    }
}
