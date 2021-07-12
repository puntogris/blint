package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Record(

    @PrimaryKey
     var recordId: String = "",

    @ColumnInfo
     var type: String = "",

    @ColumnInfo
     var traderId: String = "",

    @ColumnInfo
    var traderName :String = "",

    @ColumnInfo
     val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var amount: Int = 0,

    @ColumnInfo
    val productId: String = "",

    @ColumnInfo
    val productName:String = "",

    @ColumnInfo
    var author: String = "",

    @ColumnInfo
    var businessId:String = "",

    @ColumnInfo
    var productUnitPrice: Float = 0F,

    @ColumnInfo
    var orderId: String = "",

    @ColumnInfo
    var value: Float = 0F,

    @ColumnInfo
    var totalInStock: Int = 0,

    @ColumnInfo
    var totalOutStock:Int = 0
):Parcelable