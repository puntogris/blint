package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.puntogris.blint.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Client(

    @PrimaryKey(autoGenerate = false)
    var clientId: String = "",

    @ColumnInfo
    var name: String = "",

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
    var businessId: String = "",

    @ColumnInfo
    var debt: Float = 0F

) : Parcelable