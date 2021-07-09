package com.puntogris.blint.data.remote.deserializers

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.blint.model.*

internal object OrderDeserializer: DocumentSnapshotDeserializer<OrderWithRecords> {

    override fun deserialize(input: DocumentSnapshot?): OrderWithRecords {
        val order = Order(
            input?.get("orderId") as? String ?: "",
            input?.get("timestamp") as? Timestamp ?: Timestamp.now(),
            if (input?.get("value").toString().toFloatOrNull() == null) 0F else input?.get("value").toString().toFloat(),
            input?.get("type") as? String ?: "",
            input?.get("author") as? String ?: "",
            input?.get("traderId") as? String ?: "",
            input?.get("traderName") as? String ?: "",
            input?.get("businessId") as? String ?: "",
            if (input?.get("number").toString().toIntOrNull() == null) 0 else input?.get("number").toString().toInt()
        )

        val records = (input?.get("categories") as? List<*>)?.map {
            it as HashMap<*, *>
            FirestoreRecord(
                amount = 0,
                productId = "",
                productName = "",
                recordId = "",
                value = 0F
            )
        }

        return OrderWithRecords(order, if (records.isNullOrEmpty()) listOf() else records)
    }
}