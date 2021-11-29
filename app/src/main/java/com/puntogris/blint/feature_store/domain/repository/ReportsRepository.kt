package com.puntogris.blint.feature_store.domain.repository

import android.net.Uri
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.presentation.reports.ExportReport

interface ReportsRepository {

    suspend fun generateClientListReport(report: ExportReport): SimpleResult

    suspend fun generateSupplierListReport(report: ExportReport): SimpleResult

    suspend fun generateProductListReport(report: ExportReport): SimpleResult

    suspend fun generateClientsReport(report: ExportReport): SimpleResult

    suspend fun generateSuppliersReport(report: ExportReport): SimpleResult

    suspend fun generateProductsReport(report: ExportReport): SimpleResult
}