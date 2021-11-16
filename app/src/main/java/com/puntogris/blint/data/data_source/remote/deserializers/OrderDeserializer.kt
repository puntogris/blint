package com.puntogris.blint.data.data_source.remote.deserializers

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.puntogris.blint.model.*

internal object OrderDeserializer: DocumentSnapshotDeserializer<OrderWithRecords> {

    override fun deserialize(input: DocumentSnapshot?): OrderWithRecords {
        val order = Order(
            orderId = input?.get("orderId") as? String ?: "",
            timestamp = input?.get("timestamp") as? Timestamp ?: Timestamp.now(),
            value = if (input?.get("value").toString().toFloatOrNull() == null) 0F else input?.get("value").toString().toFloat(),
            type = input?.get("type") as? String ?: "",
            author = input?.get("author") as? String ?: "",
            traderId = input?.get("traderId") as? String ?: "",
            traderName = input?.get("traderName") as? String ?: "",
            businessId = input?.get("businessId") as? String ?: "",
            number = if (input?.get("number").toString().toIntOrNull() == null) 0 else input?.get("number").toString().toInt(),
            businessName = input?.get("businessName") as? String ?: "",
            discount = if (input?.get("discount").toString().toFloatOrNull() == null) 0F else input?.get("discount").toString().toFloat()
        )

        val records = (input?.get("records") as? List<*>)?.map {
            it as HashMap<*, *>
            FirestoreRecord(
                amount = if (it["amount"].toString().toIntOrNull() == null) 0 else it["amount"].toString().toInt(),
                productId = it["productId"].toString(),
                productName = it["productName"].toString(),
                recordId = it["recordId"].toString(),
                value = if (it["value"].toString().toFloatOrNull() == null) 0F else it["value"].toString().toFloat()
            )
        }

        val data = input?.get("debt") as? HashMap<*,*>
        val debt = if(data == null) null else FirestoreDebt(
            debtId = data["debtId"].toString(),
            amount = data["amount"].toString().toFloat(),
            type = data["type"].toString()
            )
        return OrderWithRecords(order, if (records.isNullOrEmpty()) listOf() else records, debt)
    }
}