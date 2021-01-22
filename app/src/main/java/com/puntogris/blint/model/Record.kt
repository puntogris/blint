package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Record(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    val type: String = "",

    @ColumnInfo
    val clientID: Int = 0,

    @ColumnInfo
    val supplierID :Int = 0,

    @ColumnInfo
    val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    val amount: Int = 0,

    @ColumnInfo
    val productID: Int = 0,

    @ColumnInfo
    val productName:String = "",

    @ColumnInfo
    val author: String = ""
):Parcelable