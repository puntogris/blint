package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class ProductWithDetails(

    @Embedded var product: Product = Product(),

    @Relation(
        entity = Supplier::class,
        parentColumn = "productId",
        entityColumn = "supplierId",
        associateBy = Junction(ProductSupplierCrossRef::class)
    )
    var suppliers: List<FirestoreSupplier> = emptyList(),

    @Relation(
        entity = Category::class,
        parentColumn = "productId",
        entityColumn = "categoryName",
        associateBy = Junction(ProductCategoryCrossRef::class)
    )
    var categories: List<Category> = emptyList()

) : Parcelable