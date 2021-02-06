package com.puntogris.blint.ui.reports

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.utils.ReportType

class ReportsViewModel @ViewModelInject constructor(
    private val statisticsDao: StatisticsDao
):ViewModel() {

    suspend fun getStatistics() = statisticsDao.getStatistics()

    suspend fun getReportData(reportCode:String, timeCode:String, startTime:Long, endTime:Long):ReportType{

         return when (reportCode) {
            "PRODUCTS_REPORT", "CLIENTS_REPORT", "SUPPLIERS_REPORT" -> {
                when(timeCode){
                    "WEEKLY" -> ReportType.ProductRecords(statisticsDao.getRecordsWithDays("-7 days"))
                    "MONTHLY" -> ReportType.ProductRecords(statisticsDao.getRecordsWithDays("-30 days"))
                    "QUARTERLY" -> ReportType.ProductRecords(statisticsDao.getRecordsWithDays("-90 days"))
                    "BIANNUAL" -> ReportType.ProductRecords(statisticsDao.getRecordsWithDays("-180 days"))
                    "ANNUAL" -> ReportType.ProductRecords(statisticsDao.getRecordsWithDays("-360 days"))
                    "HISTORICAL" -> ReportType.ProductRecords(statisticsDao.getAllRecords())
                    else -> ReportType.ProductRecords(statisticsDao.getRecordsWithDaysFrame(startTime, endTime))
                }
            }
            "PRODUCT_LIST_REPORT" -> ReportType.ProductList
            "CLIENT_LIST_REPORT" -> ReportType.ClientsList
            //"SUPPLIER_LIST_REPORT"
            else -> ReportType.SuppliersList
        }
    }

}