package com.puntogris.blint.feature_store.data.repository

import android.net.Uri
import com.puntogris.blint.common.framework.ExcelDrawer
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.data.data_source.local.dao.ProductsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.RecordsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.TradersDao
import com.puntogris.blint.feature_store.data.data_source.toProductRecordExcel
import com.puntogris.blint.feature_store.data.data_source.toTraderExcelList
import com.puntogris.blint.feature_store.domain.repository.ReportsRepository
import com.puntogris.blint.feature_store.presentation.reports.ExportReport
import kotlinx.coroutines.withContext

class ReportsRepositoryImpl(
    private val dispatcher: DispatcherProvider,
    private val excelDrawer: ExcelDrawer,
    private val tradersDao: TradersDao,
    private val recordsDao: RecordsDao,
    private val productsDao: ProductsDao
) : ReportsRepository {

    override suspend fun generateTradersListReport(report: ExportReport): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                val clients = tradersDao.getTraders()
                excelDrawer.drawTraderList(clients, requireNotNull(report.uri))
            }
        }

    override suspend fun generateProductListReport(report: ExportReport): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                val products = productsDao.getProducts()
                excelDrawer.drawProductsList(products, requireNotNull(report.uri))
            }
        }

    override suspend fun generateTradersReport(report: ExportReport): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                val records = when (report.timeFrame.days) {
                    0 -> recordsDao.getTradersRecords()
                    else -> recordsDao.getTraderRecordsTimeframe(report.timeFrame.days)
                }.toTraderExcelList()

                excelDrawer.drawTradersRecords(records, requireNotNull(report.uri))
            }
        }

    override suspend fun generateProductsReport(report: ExportReport) = withContext(dispatcher.io) {
        SimpleResource.build {
            val products = productsDao.getProducts()

            val records = products.mapNotNull {
                val record = recordsDao.getProductsRecordsTimeFrame(
                    it.productId,
                    report.timeFrame.days
                )
                record?.toProductRecordExcel(it)
            }
            excelDrawer.drawProductRecords(records, report.uri ?: Uri.parse(""))
        }
    }
}
