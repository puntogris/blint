package com.puntogris.blint.data.data_source.remote.deserializers

import com.google.firebase.firestore.QuerySnapshot
import com.puntogris.blint.model.ProductWithSuppliersCategories

object ProductQueryTransformation {
    fun transform(querySnapshot: QuerySnapshot):List<ProductWithSuppliersCategories>{
        return querySnapshot.map {
            ProductDeserializer.deserialize(it)
        }
    }
}