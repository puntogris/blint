package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    val barcode: String = "",

    @ColumnInfo
    val description: String = "",

    @ColumnInfo
    val amount: Int = 0,

    @ColumnInfo
    val image: String = "",

    @ColumnInfo
    val sellPrice:Int = 0,

    @ColumnInfo
    val buyPrice:Int = 0,

    @ColumnInfo
    val suggestedSellPrice: Int = 0
):Parcelable