package com.puntogris.blint.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirestoreOrder(
    var orderId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    var value: Float = 0F,
    var type:String =  "IN",
    var author:String = "",
    var traderId: String = "",
    var traderName: String = "",
    var businessId:String = "",
    var number: Int = 0,
    val records: List<FirestoreRecord> = listOf()

):Parcelable{
    companion object{
        fun from(orderWithRecords: OrderWithRecords):FirestoreOrder{
            return FirestoreOrder(
                orderId = orderWithRecords.order.orderId,
                timestamp = orderWithRecords.order.timestamp,
                value = orderWithRecords.order.value,
                type = orderWithRecords.order.type,
                author = orderWithRecords.order.author,
                traderId = orderWithRecords.order.traderName,
                traderName = orderWithRecords.order.traderName,
                businessId = orderWithRecords.order.businessId,
                number = orderWithRecords.order.number,
                records = orderWithRecords.records
            )
        }
    }


}