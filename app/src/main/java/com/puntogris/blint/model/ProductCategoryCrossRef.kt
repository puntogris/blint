package com.puntogris.blint.model

import androidx.room.Entity

@Entity(primaryKeys = ["productId", "categoryId"])
data class ProductCategoryCrossRef(
    val productId: String,
    val categoryId: String
)