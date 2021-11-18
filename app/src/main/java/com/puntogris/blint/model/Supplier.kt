package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Supplier(

    @PrimaryKey(autoGenerate = true)
    var supplierId: Int = 0,

    @ColumnInfo
    var companyName: String = "",

    @ColumnInfo
    val companyPhone: String = "",

    @ColumnInfo
    val address: String = "",

    @ColumnInfo
    val companyEmail: String = "",

    @ColumnInfo
    val sellerName: String = "",

    @ColumnInfo
    val sellerEmail: String = "",

    @ColumnInfo
    val sellerPhone: String = "",

    @ColumnInfo
    val companyPaymentInfo: String = "",

    @ColumnInfo
    val notes: String = "",

    @ColumnInfo
    var businessId: Int = 0,

    @ColumnInfo
    var debt: Float = 0F

) : Parcelable