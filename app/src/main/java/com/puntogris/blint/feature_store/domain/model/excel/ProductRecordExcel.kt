package com.puntogris.blint.feature_store.domain.model.excel

data class ProductRecordExcel(
    val name: String = "",
    var historicInStock: Int = 0,
    var historicOutStock: Int = 0
)
