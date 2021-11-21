package com.puntogris.blint.model.product

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.puntogris.blint.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@IgnoreExtraProperties
@Keep
data class Product(

    @PrimaryKey(autoGenerate = false)
    var productId: String = "",

    @ColumnInfo
    var name: String = "",

    @ColumnInfo
    var barcode: String = "",

    @ColumnInfo
    val description: String = "",

    @ColumnInfo
    var amount: Int = 0,

    @ColumnInfo
    var image: String = "",

    @ColumnInfo
    val sellPrice: Float = 0F,

    @ColumnInfo
    val buyPrice: Float = 0F,

    @ColumnInfo
    val suggestedSellPrice: Float = 0F,

    @ColumnInfo
    var sku: String = "",

    @ColumnInfo
    val brand: String = "",

    @ColumnInfo
    val size: String = "",

    @ColumnInfo
    var lastRecordTimestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var historicInStock: Int = 0,

    @ColumnInfo
    var historicOutStock: Int = 0,

    @ColumnInfo
    var businessId: String = "",

    @ColumnInfo
    val minStock: Int = 0

) : Parcelable