package com.puntogris.blint.feature_store.presentation.reports

import com.puntogris.blint.R

enum class ReportType(val res: Int) {
    ClientsList(R.string.report_list_clients),
    SuppliersList(R.string.report_list_suppliers),
    ProductsList(R.string.report_list_products),
    ClientsRecords(R.string.report_clients_records),
    SuppliersRecords(R.string.report_suppliers_records),
    ProductRecords(R.string.report_products_records)
}

