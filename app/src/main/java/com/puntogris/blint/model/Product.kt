package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@IgnoreExtraProperties
@Keep
data class Product(

    @PrimaryKey(autoGenerate = true)
    var productId: Int = 0,

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
    var totalInStock: Int = 0,

    @ColumnInfo
    var totalOutStock: Int = 0,

    @ColumnInfo
    var businessId: Int = 0,

    @ColumnInfo
    val minStock: Int = 0

) : Parcelable