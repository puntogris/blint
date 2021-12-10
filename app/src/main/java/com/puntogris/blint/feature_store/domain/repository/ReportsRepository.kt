package com.puntogris.blint.feature_store.domain.repository

import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.presentation.reports.ExportReport

interface ReportsRepository {

    suspend fun generateClientListReport(report: ExportReport): SimpleResource

    suspend fun generateSupplierListReport(report: ExportReport): SimpleResource

    suspend fun generateProductListReport(report: ExportReport): SimpleResource

    suspend fun generateClientsReport(report: ExportReport): SimpleResource

    suspend fun generateSuppliersReport(report: ExportReport): SimpleResource

    suspend fun generateProductsReport(report: ExportReport): SimpleResource
}