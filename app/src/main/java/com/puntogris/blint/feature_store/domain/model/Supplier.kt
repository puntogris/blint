package com.puntogris.blint.feature_store.domain.model

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

    @PrimaryKey(autoGenerate = false)
    var supplierId: String = "",

    @ColumnInfo
    var companyName: String = "",

    @ColumnInfo
    var companyPhone: String = "",

    @ColumnInfo
    var address: String = "",

    @ColumnInfo
    var companyEmail: String = "",

    @ColumnInfo
    var sellerName: String = "",

    @ColumnInfo
    var sellerEmail: String = "",

    @ColumnInfo
    var sellerPhone: String = "",

    @ColumnInfo
    var companyPaymentInfo: String = "",

    @ColumnInfo
    var notes: String = "",

    @ColumnInfo
    var businessId: String = "",

    @ColumnInfo
    var debt: Float = 0F

) : Parcelable