package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Orders")
data class Order(

    @PrimaryKey
    var orderId: String = "",

    @ColumnInfo
    val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var value: Float = 0F,

    @ColumnInfo
    var type:String =  "IN",

    @ColumnInfo
    var author:String = "",

    @ColumnInfo
    var traderId: String = "",

    @ColumnInfo
    var traderName: String = "",

    @ColumnInfo
    var businessId:String = "",

    @ColumnInfo
    var number: Int = 0

):Parcelable{
    @Ignore
    @IgnoredOnParcel
    var items:List<Record> = listOf()

    fun updateOrderValue(){
        value = items.sumByDouble { it.value.toDouble() }.toFloat()
    }

    fun updateOrderData(user: RoomUser, orderCollection: CollectionReference, recordCollection: CollectionReference){
        businessId = user.currentBusinessId
        orderId = orderCollection.document().id
        author = user.currentUid
        items.forEach {
            it.recordId = recordCollection.document().id
            it.type = type
            it.author = author
            it.traderName = traderName
            it.traderId = traderId
            it.orderId = orderId
            it.businessId = businessId
        }
    }
}