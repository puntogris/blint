package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Record(

    @PrimaryKey(autoGenerate = true)
    var recordId: Int = 0,

    @ColumnInfo
    var type: String = "",

    @ColumnInfo
    var traderId: Int = 0,

    @ColumnInfo
    var traderName: String = "",

    @ColumnInfo
    val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var amount: Int = 0,

    @ColumnInfo
    val productId: Int = 0,

    @ColumnInfo
    val productName: String = "",

    @ColumnInfo
    var author: String = "",

    @ColumnInfo
    var businessId: String = "",

    @ColumnInfo
    var productUnitPrice: Float = 0F,

    @ColumnInfo
    var orderId: Int = 0,

    @ColumnInfo
    var value: Float = 0F,

    @ColumnInfo
    var totalInStock: Int = 0,

    @ColumnInfo
    var totalOutStock: Int = 0,

    @ColumnInfo
    var sku: String = "",

    @ColumnInfo
    @get:Exclude var barcode: String = ""
) : Parcelable