package com.puntogris.blint.feature_store.presentation.reports

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.getDateFormattedString
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.repository.ReportsRepository
import com.puntogris.blint.feature_store.presentation.reports.ReportType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: ReportsRepository
) : ViewModel() {

    private val report = ExportReport()

    fun updateTimeFrame(timeFrame: TimeFrame) {
        report.timeFrame = timeFrame
    }

    fun updateReportType(reportType: ReportType) {
        report.type = reportType
    }

    fun updateReportUri(uri: Uri) {
        report.uri = uri
    }

    suspend fun generateReport(): SimpleResult {
        return when (report.type) {
            ClientsList -> repository.generateClientListReport(report)
            ClientsRecords -> repository.generateClientsReport(report)
            ProductsList -> repository.generateProductListReport(report)
            ProductRecords -> repository.generateProductsReport(report)
            SuppliersList -> repository.generateSupplierListReport(report)
            SuppliersRecords -> repository.generateSuppliersReport(report)
            else -> SimpleResult.Failure
        }
    }

    fun getReportName(context: Context): String {
        return report.type.takeIf { it != null }?.let {
            context.getString(it.res, Date().getDateFormattedString())
        } ?: context.getString(R.string.snack_an_error_occurred)
    }

    fun getReportUri() = report.uri
}
