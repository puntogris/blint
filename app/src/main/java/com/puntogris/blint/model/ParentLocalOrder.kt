package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity
class ParentLocalOrder (

    @PrimaryKey(autoGenerate = true)
    val orderId: Int,

    @ColumnInfo
    val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    val value: Int = 0,

    @ColumnInfo
    val type:String =  "",

    @ColumnInfo
    val author:String = "",

    @ColumnInfo
    val traderId: Int = 0,

    @ColumnInfo
    val traderName: String = ""
)