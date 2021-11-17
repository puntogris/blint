package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FirestoreOrder(
    var orderId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    var value: Float = 0F,
    var type:String =  "IN",
    var author:String = "",
    var traderId: Int = 0,
    var traderName: String = "",
    var businessId:String = "",
    var number: Int = 1,
    val records: List<FirestoreRecord> = listOf(),
    val debt: FirestoreDebt? = null,
    val businessName: String = "",
    val discount: Float = 0F

):Parcelable{
    companion object{
        fun from(orderWithRecords: OrderWithRecords): FirestoreOrder{
            return FirestoreOrder(
                orderId = orderWithRecords.order.orderId,
                timestamp = orderWithRecords.order.timestamp,
                value = orderWithRecords.order.value,
                type = orderWithRecords.order.type,
                author = orderWithRecords.order.author,
                traderId = orderWithRecords.order.traderId,
                traderName = orderWithRecords.order.traderName,
                businessId = orderWithRecords.order.businessId,
                number = orderWithRecords.order.number,
                records = orderWithRecords.records,
                debt = orderWithRecords.debt,
                businessName = orderWithRecords.order.businessName,
                discount = orderWithRecords.order.discount
            )
        }
    }


}