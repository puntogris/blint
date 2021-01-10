package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Client(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo
    val name:String = "",

    @ColumnInfo
    val address:String ="",

    @ColumnInfo
    val email:String = "",

    @ColumnInfo
    val phone:String = "",

    @ColumnInfo
    val paymentInfo:String = "",

    @ColumnInfo
    val discount: Float = 0F

):Parcelable