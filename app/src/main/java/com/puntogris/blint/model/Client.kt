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
data class Client(

    @PrimaryKey(autoGenerate = true)
    var clientId: Int = 0,

    @ColumnInfo
    var name:String = "",

    @ColumnInfo
    val address:String ="",

    @ColumnInfo
    val email:String = "",

    @ColumnInfo
    val phone:String = "",

    @ColumnInfo
    val paymentInfo:String = "",

    @ColumnInfo
    val discount: Float = 0F,

    @ColumnInfo
    var businessId:String = "",

    @ColumnInfo
    var debt: Float = 0F

):Parcelable