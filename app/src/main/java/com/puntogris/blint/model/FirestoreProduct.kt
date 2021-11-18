package com.puntogris.blint.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class FirestoreProduct(
    val productId: Int = 0,
    val name: String = "",
    val barcode: String = "",
    val description: String = "",
    val amount: Int = 0,
    val image: String = "",
    val sellPrice: Float = 0F,
    val buyPrice: Float = 0F,
    val suggestedSellPrice: Float = 0F,
    val sku: String = "",
    val brand: String = "",
    val size: String = "",
    val lastRecordTimestamp: Timestamp = Timestamp.now(),
    val totalInStock: Int = 0,
    val totalOutStock: Int = 0,
    val businessId: Int = 0,
    val minStock: Int = 0,
    val suppliers: List<FirestoreSupplier>? = null,
    val categories: List<String>? = null,
    val search_name: List<String> = listOf()
) {

    companion object {
        fun from(product: ProductWithDetails): FirestoreProduct {
            return FirestoreProduct(
                product.product.productId,
                product.product.name,
                product.product.barcode,
                product.product.description,
                product.product.amount,
                product.product.image,
                product.product.sellPrice,
                product.product.buyPrice,
                product.product.suggestedSellPrice,
                product.product.sku,
                product.product.brand,
                product.product.size,
                product.product.lastRecordTimestamp,
                product.product.totalInStock,
                product.product.totalOutStock,
                product.product.businessId,
                product.product.minStock,
                product.suppliers?.map {
                    FirestoreSupplier(
                        companyName = it.companyName,
                        supplierId = it.supplierId
                    )
                },
                product.categories?.map {
                    it.categoryName
                },
                createSearchName(product.product.name)
            )
        }

        private fun createSearchName(text: String): List<String> {
            val searchList = mutableListOf<String>()
            text.forEachIndexed { index, c ->
                if (!c.isWhitespace()) {
                    for (i in 1..text.length - index) {
                        searchList.add(text.substring(index, index + i))
                    }
                }
            }
            return searchList.distinct()
        }
    }
}