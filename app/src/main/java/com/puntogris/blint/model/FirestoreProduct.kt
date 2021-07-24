package com.puntogris.blint.model

import com.google.firebase.Timestamp
import java.util.*

data class FirestoreProduct(
    val productId: String = "",
    val name: String = "",
    val barcode: String = "",
    val description: String = "",
    val amount: Int = 0,
    val image: String = "",
    val sellPrice:Float = 0F,
    val buyPrice:Float = 0F,
    val suggestedSellPrice: Float = 0F,
    val internalCode: String = "",
    val brand:String = "",
    val size:String = "",
    val lastRecordTimestamp: Timestamp = Timestamp.now(),
    val totalInStock: Int = 0,
    val totalOutStock: Int = 0,
    val businessId:String = "",
    val suppliers:List<FirestoreSupplier>? = null,
    val categories:List<String>? = null,
    val search_name:List<String> = listOf(),
    var search_categories:List<String> = listOf()
){

    companion object{
        fun from(product: ProductWithSuppliersCategories): FirestoreProduct{
            val firestore =
                FirestoreProduct(
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
            val list = mutableListOf<String>()
            firestore.categories?.forEach {
                list.addAll(createSearchName(it))
            }

            list.addAll(createSearchName(UUID.randomUUID().toString()))

            firestore.search_categories = list.distinct()
            return firestore
        }

        private fun createSearchName(text:String): List<String>{
            val searchList = mutableListOf<String>()
            text.forEachIndexed { index, c ->
                if(!c.isWhitespace()){
                    for (i in 1..text.length - index){
                        searchList.add(text.substring(index, index + i))
                    }
                }
            }
            return searchList.distinct()
        }
    }
}