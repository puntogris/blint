package com.puntogris.blint.feature_store.domain.model.product

import androidx.room.Entity

@Entity(primaryKeys = ["productId", "supplierId"])
data class ProductSupplierCrossRef(
    val productId: String,
    val supplierId: String
)