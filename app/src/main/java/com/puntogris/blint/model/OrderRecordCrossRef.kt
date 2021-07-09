package com.puntogris.blint.model

import androidx.room.Entity

@Entity(primaryKeys = ["orderId", "recordId"])
class OrderRecordCrossRef(
    val orderId: String,
    val recordId: String
)
