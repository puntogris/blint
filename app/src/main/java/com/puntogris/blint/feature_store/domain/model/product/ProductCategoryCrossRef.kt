package com.puntogris.blint.feature_store.domain.model.product

import androidx.room.Entity

@Entity(primaryKeys = ["productId", "categoryName"])
data class ProductCategoryCrossRef(
    val productId: String,
    val categoryName: String
)