package com.puntogris.blint.feature_store.domain.repository

import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.presentation.reports.ExportReport

interface ReportsRepository {

    suspend fun generateTradersListReport(report: ExportReport): SimpleResource

    suspend fun generateProductListReport(report: ExportReport): SimpleResource

    suspend fun generateTradersReport(report: ExportReport): SimpleResource

    suspend fun generateProductsReport(report: ExportReport): SimpleResource
}