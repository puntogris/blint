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
        val product = Product(
            input?.get("productId") as? String ?: "",
            input?.get("name") as? String ?: "",
            input?.get("barcode") as? String ?: "",
            input?.get("description") as? String ?: "",
            input?.get("amount") as? Int ?: 0,
            input?.get("image") as? String ?: "",
            input?.get("sellPrice") as? Float ?: 0F,
            input?.get("buyPrice") as? Float ?: 0F,
            input?.get("suggestedSellPrice") as? Float ?: 0F,
            input?.get("internalCode") as? String ?: "",
            input?.get("brand") as? String ?: "",
            input?.get("size") as? String ?: "",
            input?.get("lastRecordTimestamp") as? Timestamp ?: Timestamp.now(),
            input?.get("totalInStock") as? Int ?: 0,
            input?.get("totalOutStock") as? Int ?: 0,
            input?.get("businessId") as? String ?: "",
        )
        val categories = (input?.get("categories") as? List<*>)?.map {
            it as HashMap<*, *>
            Category(name = it["name"].toString())
        }

        val suppliers = (input?.get("categories") as? List<*>)?.map {
            it as HashMap<*, *>
            Supplier(
                companyName = it["companyName"].toString(),
                supplierId = it["supplierId"].toString()
            )
        }

        return ProductWithSuppliersCategories(
            product,
            suppliers ?: emptyList(),
            categories ?: emptyList())
    }
}