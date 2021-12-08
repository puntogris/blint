package com.puntogris.blint.feature_store.domain.model.order

import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.UUIDGenerator

data class NewOrder(

    val orderId: String = UUIDGenerator.randomUUID(),

    var type: String = Constants.IN,

    var traderId: String = "",

    var traderName: String = "",

    var newRecords: List<NewRecord> = emptyList(),

    var newDebt: NewDebt? = null,

    var value: Float = 0F
)

data class NewRecord(

    val recordId: String = UUIDGenerator.randomUUID(),

    var amount: Int = 0,

    var productId: String = "",

    var productName: String = "",

    var productUnitPrice: Float = 0F,

    var historicalInStock: Int = 0,

    var historicalOutStock: Int = 0,

    var sku: String = "",

    var barcode: String = "",

    var currentStock: Int = 0
)

data class NewDebt(

    val debtId: String = UUIDGenerator.randomUUID(),

    var amount: Float = 0F,

    var traderType: String = ""
)

fun NewOrder.updateOrderTotalValue() {
    value = newRecords.sumOf { (it.productUnitPrice * it.amount).toInt() }.toFloat()
}

fun NewOrder.areRecordsValid(): Boolean {
    return newRecords.isNotEmpty() && newRecords.all { it.amount > 0 && (type == Constants.IN || it.amount <= it.currentStock) }
}