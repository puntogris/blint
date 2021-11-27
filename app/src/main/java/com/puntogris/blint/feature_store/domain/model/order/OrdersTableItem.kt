package com.puntogris.blint.feature_store.domain.model.order

data class OrdersTableItem(
    val productName: String,
    val amount: Int, val value: Float
)