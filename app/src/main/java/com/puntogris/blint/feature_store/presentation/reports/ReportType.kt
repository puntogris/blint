package com.puntogris.blint.feature_store.presentation.reports

import com.puntogris.blint.R

enum class ReportType(val res: Int) {
    ClientsList(R.string.report_list_clients),
    ProductsList(R.string.report_list_products),
    ClientsRecords(R.string.report_traders_records),
    ProductRecords(R.string.report_products_records)
}

