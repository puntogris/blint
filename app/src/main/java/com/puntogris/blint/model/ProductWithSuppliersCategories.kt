package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductWithSuppliersCategories(

    @Embedded var product: Product = Product(),

    @Relation(
        entity = Supplier::class,
        parentColumn = "productId",
        entityColumn = "supplierId",
        associateBy = Junction(ProductSupplierCrossRef::class))
    var suppliers:List<FirestoreSupplier>? = null,

    @Relation(
        entity = Category::class,
        parentColumn = "productId",
        entityColumn = "categoryId",
        associateBy = Junction(ProductCategoryCrossRef::class)
    )
    var categories:List<FirestoreCategory>? = null
):Parcelable