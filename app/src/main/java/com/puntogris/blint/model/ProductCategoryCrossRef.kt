package com.puntogris.blint.model

import androidx.room.Entity

@Entity(primaryKeys = ["productId", "categoryName"])
data class ProductCategoryCrossRef(
    val productId: String,
    val categoryName: String
)