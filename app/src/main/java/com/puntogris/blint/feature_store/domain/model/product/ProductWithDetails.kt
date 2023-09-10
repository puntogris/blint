package com.puntogris.blint.feature_store.domain.model.product

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.model.Trader
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class ProductWithDetails(

    @Embedded var product: Product = Product(),

    @Relation(
        entity = Trader::class,
        parentColumn = "productId",
        entityColumn = "traderId",
        associateBy = Junction(ProductSupplierCrossRef::class)
    )
    var traders: List<Trader> = emptyList(),

    @Relation(
        entity = Category::class,
        parentColumn = "productId",
        entityColumn = "categoryName",
        associateBy = Junction(ProductCategoryCrossRef::class)
    )
    var categories: List<Category> = emptyList()

) : Parcelable
