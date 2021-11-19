package com.puntogris.blint.model.order

import androidx.room.Entity

@Entity(primaryKeys = ["orderId", "recordId"])
data class OrderRecordCrossRef(
    val orderId: Int,
    val recordId: Int
)
