package com.puntogris.blint.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity
data class ProductWithSuppliersCategories(
    @Embedded val product: Product,

    @Relation(
        parentColumn = "productId",
        entityColumn = "supplierId",
        associateBy = Junction(ProductSupplierCrossRef::class)
    )
    val suppliers:List<Supplier>,

    @Relation(
        parentColumn = "productId",
        entityColumn = "categoryId",
        associateBy = Junction(ProductCategoryCrossRef::class)
    )
    val categories:List<Category>
)