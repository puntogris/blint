package com.puntogris.blint.feature_store.domain.model.order

import androidx.room.Entity

@Entity(primaryKeys = ["orderId", "recordId"])
data class OrderRecordCrossRef(
    val orderId: String,
    val recordId: String
)
