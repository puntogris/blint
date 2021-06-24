package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Orders")
data class Order(

    @PrimaryKey(autoGenerate = true)
    var orderId: Int = 0,

    @ColumnInfo
    val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var value: Float = 0F,

    @ColumnInfo
    var type:String =  "IN",

    @ColumnInfo
    val author:String = "",

    @ColumnInfo
    var traderId: Int = 0,

    @ColumnInfo
    var traderName: String = "",

    @ColumnInfo
    var businessId:String = "",


):Parcelable{
    @Ignore
    @IgnoredOnParcel
    var items:List<Record> = listOf()
}