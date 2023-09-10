package com.puntogris.blint.feature_store.domain.model.product

import androidx.room.Entity

@Entity(primaryKeys = ["productId", "traderId"])
data class ProductSupplierCrossRef(
    val productId: String,
    val traderId: String
)
