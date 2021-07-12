package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Debt(

    @PrimaryKey
    var debtId: String = "",

    @ColumnInfo
    var amount: Float = 0F,

    @ColumnInfo
    var orderId: String = "",

    @ColumnInfo
    var traderId: String = "",

    @ColumnInfo
    var traderName :String = "",

    @ColumnInfo
    var timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var author: String = "",

    @ColumnInfo
    var businessId: String = "",

    @ColumnInfo
    var type: String = ""
):Parcelable