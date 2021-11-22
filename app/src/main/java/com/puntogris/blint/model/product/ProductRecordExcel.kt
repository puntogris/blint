package com.puntogris.blint.model.product

data class ProductRecordExcel(
    val name: String = "",
    var historicInStock: Int = 0,
    var historicOutStock: Int = 0
)