package com.puntogris.blint.feature_store.domain.model.excel

class ClientRecordExcel(
    val clientName: String = "",
    val products: List<ProductRecord> = listOf()
)
