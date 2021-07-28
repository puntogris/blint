package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
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
        entityColumn = "categoryName",
        associateBy = Junction(ProductCategoryCrossRef::class)
    )
    var categories:List<Category>? = null
):Parcelable