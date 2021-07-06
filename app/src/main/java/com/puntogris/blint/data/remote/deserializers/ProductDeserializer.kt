package com.puntogris.blint.data.remote.deserializers

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.model.Supplier


internal object ProductDeserializer :
    DocumentSnapshotDeserializer<ProductWithSuppliersCategories> {

    override fun deserialize(input: DocumentSnapshot?): ProductWithSuppliersCategories {
     //   if (input == null) return ProductWithSuppliersCategories()
        val productId = input?.get("productId").toString()
        val name = input?.get("name").toString()
        val barcode = input?.get("barcode").toString()
        val description = input?.get("description").toString()
        val amount = input?.get("amount").toString().toInt()
        val image = input?.get("image").toString()
        val sellPrice = input?.get("sellPrice").toString().toFloat()
        val buyPrice = input?.get("buyPrice").toString().toFloat()
        val suggestedSellPrice = input?.get("suggestedSellPrice").toString().toFloat()
        val internalCode = input?.get("internalCode").toString()
        val brand = input?.get("brand").toString()
        val size = input?.get("size").toString()
        val lastRecordTimestamp = if (input?.get("lastRecordTimestamp") as? Timestamp != null) input.get("lastRecordTimestamp") as? Timestamp else Timestamp.now()
        val businessId = input?.get("businessId").toString()
        val totalInStock = input?.get("totalInStock").toString().toInt()
        val totalOutStock = input?.get("totalOutStock").toString().toInt()

//        val product = Product(
//            productId,
//            name,
//            barcode,
//            description,
//            amount,
//            image,
//            sellPrice,
//            buyPrice,
//            suggestedSellPrice,
//            internalCode,
//            brand,
//            size,
//            lastRecordTimestamp,
//            totalInStock,
//            totalOutStock,
//            businessId
//        )

        val product = input?.toObject(Product::class.java)
        val categoriesData = input?.get("categories") as List<String>?

        val categories = categoriesData?.map {
            Category(
                categoryId = "",
                name = it
            )
        }

        val suppliersData = input?.get("categories") as List<String>?

        val suppliers = suppliersData?.map {
            Supplier(
                supplierId = "",
                companyName = it
            )
        }

        return ProductWithSuppliersCategories(product ?: Product(), suppliers, categories)
    }
}