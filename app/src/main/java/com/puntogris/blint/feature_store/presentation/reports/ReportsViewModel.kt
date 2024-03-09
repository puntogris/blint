package com.puntogris.blint.feature_store.presentation.reports

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.getDateFormattedString
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.repository.ReportsRepository
import com.puntogris.blint.feature_store.presentation.reports.ReportType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: ReportsRepository
) : ViewModel() {

    private val report = ExportReport()

    fun getReportUri() = report.uri

    fun updateTimeFrame(timeFrame: TimeFrame) {
        report.timeFrame = timeFrame
    }

    fun updateReportType(reportType: ReportType) {
        report.type = reportType
    }

    fun updateReportUri(uri: Uri) {
        report.uri = uri
    }

    suspend fun generateReport(): SimpleResource {
        return when (report.type) {
            ClientsList -> repository.generateTradersListReport(report)
            ClientsRecords -> repository.generateTradersReport(report)
            ProductsList -> repository.generateProductListReport(report)
            ProductRecords -> repository.generateProductsReport(report)
            else -> Resource.Error()
        }
    }

    fun getReportName(context: Context): String {
        return report.type?.let {
            context.getString(it.res, OffsetDateTime.now().getDateFormattedString())
        } ?: context.getString(R.string.snack_an_error_occurred)
    }
}
