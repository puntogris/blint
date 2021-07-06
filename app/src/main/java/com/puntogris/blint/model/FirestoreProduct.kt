package com.puntogris.blint.model

import com.google.firebase.Timestamp

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
    val suppliers:List<Supplier>? = null,
    val categories:List<Category>? = null
){

    companion object{
        fun from(product: ProductWithSuppliersCategories): FirestoreProduct{
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
                product.product.internalCode,
                product.product.brand,
                product.product.size,
                product.product.lastRecordTimestamp,
                product.product.totalInStock,
                product.product.totalOutStock,
                product.product.businessId,
                product.suppliers,
                product.categories
            )
        }
    }
}