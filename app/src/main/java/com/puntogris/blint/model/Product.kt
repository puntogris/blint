package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Product(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    val barcode: String = "",

    @ColumnInfo
    val description: String = "",

    @ColumnInfo
    val amount: Float = 0F,

    @ColumnInfo
    var image: HashMap<String, String> = hashMapOf("uri" to "", "path" to ""),

    @ColumnInfo
    val sellPrice:Float = 0F,

    @ColumnInfo
    val buyPrice:Float = 0F,

    @ColumnInfo
    val suggestedSellPrice: Float = 0F
):Parcelable