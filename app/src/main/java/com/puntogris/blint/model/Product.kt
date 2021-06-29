package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Product(
    @PrimaryKey
    var productId: String = "",

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    var barcode: String = "",

    @ColumnInfo
    val description: String = "",

    @ColumnInfo
    var amount: Int = 0,

    @ColumnInfo
    var image: HashMap<String, String> = hashMapOf("uri" to "", "path" to ""),

    @ColumnInfo
    val sellPrice:Float = 0F,

    @ColumnInfo
    val buyPrice:Float = 0F,

    @ColumnInfo
    val suggestedSellPrice: Float = 0F,

    @ColumnInfo
    val internalCode: String = "",

    @ColumnInfo
    val brand:String = "",

    @ColumnInfo
    val size:String = "",

    @ColumnInfo
    var lastRecordTimestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var totalInStock: Int = 0,

    @ColumnInfo
    var totalOutStock: Int = 0,

    @ColumnInfo
    var businessId:String = ""
):Parcelable