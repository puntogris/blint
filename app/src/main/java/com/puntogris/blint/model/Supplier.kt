package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Supplier(

    @PrimaryKey(autoGenerate = true)
    var supplierId: Int = 0,

    @ColumnInfo
    val companyName: String = "",

    @ColumnInfo
    val companyPhone:String = "",

    @ColumnInfo
    val address: String = "",

    @ColumnInfo
    val companyEmail:String = "",

    @ColumnInfo
    val sellerName:String = "",

    @ColumnInfo
    val sellerEmail:String = "",

    @ColumnInfo
    val sellerPhone:String = "",

    @ColumnInfo
    val companyPaymentInfo: String = "",

    @ColumnInfo
    val notes:String = ""

):Parcelable