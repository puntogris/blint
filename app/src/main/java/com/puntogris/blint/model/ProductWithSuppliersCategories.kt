package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class ProductWithSuppliersCategories(
    @Embedded val product: Product,

    @Relation(
        parentColumn = "productId",
        entityColumn = "supplierId",
        associateBy = Junction(ProductSupplierCrossRef::class)
    )
    val suppliers:List<Supplier>? = null,

    @Relation(
        parentColumn = "productId",
        entityColumn = "categoryId",
        associateBy = Junction(ProductCategoryCrossRef::class)
    )
    val categories:List<Category>? = null
):Parcelable