package com.puntogris.blint.feature_store.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.puntogris.blint.common.utils.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Trader(

    @PrimaryKey(autoGenerate = false)
    var traderId: String = "",

    @ColumnInfo
    var name: String = "",

    @ColumnInfo
    var type: String = Constants.OTHER,

    @ColumnInfo
    var address: String = "",

    @ColumnInfo
    var email: String = "",

    @ColumnInfo
    var phone: String = "",

    @ColumnInfo
    var paymentInfo: String = "",

    @ColumnInfo
    var discount: Float = 0F,

    @ColumnInfo
    var storeId: String = "",

    @ColumnInfo
    var debt: Float = 0F,

    @ColumnInfo
    var notes: String = ""

) : Parcelable
