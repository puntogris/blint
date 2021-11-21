package com.puntogris.blint.model.product

import androidx.room.Entity

@Entity(primaryKeys = ["productId", "supplierId"])
data class ProductSupplierCrossRef(
    val productId: String,
    val supplierId: String
)