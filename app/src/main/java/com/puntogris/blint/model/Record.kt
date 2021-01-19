package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity
data class Record(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    val type:Int = 0,

    @ColumnInfo
    val clients:List<Int> = listOf(),

    @ColumnInfo
    val suppliers: List<Int> = listOf(),

    @ColumnInfo
    val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    val amount: Int = 0,

    @ColumnInfo
    val product: Int = 0,

    @ColumnInfo
    val productName:String
)