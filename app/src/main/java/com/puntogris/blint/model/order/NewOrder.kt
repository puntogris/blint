package com.puntogris.blint.model.order

import com.puntogris.blint.utils.UUIDGenerator

class NewOrder(

    val orderId: String = UUIDGenerator.randomUUID(),

    var type: String = "IN",

    var traderId: String = "",

    var traderName: String = "",

    var discount: Float = 0F,

    var newRecords: List<NewRecord> = emptyList(),

    var newDebt: NewDebt? = null
)

class NewRecord(

    val recordId: String = UUIDGenerator.randomUUID(),

    var amount: Int = 0,

    var productId: String = "",

    var productName: String = "",

    var totalInStock: Int = 0,

    var totalOutStock: Int = 0,

    var sku: String = "",

    var barcode: String = ""
)

class NewDebt(

    val debtId: String = UUIDGenerator.randomUUID(),

    var amount: Float = 0F,

    var traderType: String = ""
)