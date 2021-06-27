package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Record(

    @PrimaryKey
    val recordId: String = "",

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
    var value: Float = 0F
):Parcelable