package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class ProductWithSuppliersCategories(

    @Embedded var product: Product = Product(),

    @Relation(
        parentColumn = "productId",
        entityColumn = "supplierId",
        associateBy = Junction(ProductSupplierCrossRef::class)
    )
    var suppliers:List<FirestoreSupplier>? = null,

    @Relation(
        parentColumn = "productId",
        entityColumn = "categoryId",
        associateBy = Junction(ProductCategoryCrossRef::class)
    )
    var categories:List<FirestoreCategory>? = null
):Parcelable