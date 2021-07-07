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
    @get:Exclude var recordId: String = "",

    @ColumnInfo
    @get:Exclude var type: String = "",

    @ColumnInfo
    @get:Exclude var traderId: String = "",

    @ColumnInfo
    @get:Exclude var traderName :String = "",

    @ColumnInfo
    @get:Exclude val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var amount: Int = 0,

    @ColumnInfo
    val productId: String = "",

    @ColumnInfo
    val productName:String = "",

    @ColumnInfo
    @get:Exclude var author: String = "",

    @ColumnInfo
    @get:Exclude var businessId:String = "",

    @ColumnInfo
    @get:Exclude var productUnitPrice: Float = 0F,

    @ColumnInfo
    @get:Exclude var orderId: String = "",

    @ColumnInfo
    var value: Float = 0F
):Parcelable