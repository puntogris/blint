package com.puntogris.blint.model.order

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.puntogris.blint.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Record(

    @PrimaryKey(autoGenerate = false)
    var recordId: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    var type: String = "",

    @ColumnInfo
    var traderId: String = "",

    @ColumnInfo
    var traderName: String = "",

    @ColumnInfo
    val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var amount: Int = 0,

    @ColumnInfo
    var productId: String = "",

    @ColumnInfo
    var productName: String = "",

    @ColumnInfo
    var businessId: String = "",

    @ColumnInfo
    var productUnitPrice: Float = 0F,

    @ColumnInfo
    var orderId: String = "",

    @ColumnInfo
    var value: Float = 0F,

    @ColumnInfo
    var totalInStock: Int = 0,

    @ColumnInfo
    var totalOutStock: Int = 0,

    @ColumnInfo
    var sku: String = "",

    @ColumnInfo
    var barcode: String = ""
) : Parcelable