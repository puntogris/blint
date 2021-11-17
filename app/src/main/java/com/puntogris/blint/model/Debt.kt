package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Debt(

    @PrimaryKey(autoGenerate = true)
    var debtId: Int = 0,

    @ColumnInfo
    var amount: Float = 0F,

    @ColumnInfo
    var orderId: Int = 0,

    @ColumnInfo
    var traderId: Int = 0,

    @ColumnInfo
    var traderName: String = "",

    @ColumnInfo
    var timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var author: String = "",

    @ColumnInfo
    var businessId: String = "",

    @ColumnInfo
    var type: String = ""
) : Parcelable