package com.puntogris.blint.data.remote.deserializers

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.blint.model.*

internal object ProductDeserializer :
    DocumentSnapshotDeserializer<ProductWithSuppliersCategories> {

    override fun deserialize(input: DocumentSnapshot?): ProductWithSuppliersCategories {
        val product = Product(
            input?.get("productId") as? String ?: "",
            input?.get("name") as? String ?: "",
            input?.get("barcode") as? String ?: "",
            input?.get("description") as? String ?: "",
            if (input?.get("amount").toString().toIntOrNull() == null) 0 else input?.get("amount").toString().toInt(),
            input?.get("image") as? String ?: "",
            if (input?.get("sellPrice").toString().toFloatOrNull() == null) 0F else input?.get("sellPrice").toString().toFloat(),
            if (input?.get("buyPrice").toString().toFloatOrNull() == null) 0F else input?.get("buyPrice").toString().toFloat(),
            if (input?.get("suggestedSellPrice").toString().toFloatOrNull() == null) 0F else input?.get("suggestedSellPrice").toString().toFloat(),
            input?.get("internalCode") as? String ?: "",
            input?.get("brand") as? String ?: "",
            input?.get("size") as? String ?: "",
            input?.get("lastRecordTimestamp") as? Timestamp ?: Timestamp.now(),
            if (input?.get("totalInStock").toString().toIntOrNull() == null) 0 else input?.get("totalInStock").toString().toInt(),
            if (input?.get("totalOutStock").toString().toIntOrNull() == null) 0 else input?.get("totalOutStock").toString().toInt(),
            input?.get("businessId") as? String ?: "",
        )

        val categories = (input?.get("categories") as? List<*>)?.map {
            it as HashMap<*, *>
            FirestoreCategory(name = it["name"].toString(), categoryId = it["categoryId"].toString())
        }

        val suppliers = (input?.get("suppliers") as? List<*>)?.map {
            it as HashMap<*, *>
            FirestoreSupplier(
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