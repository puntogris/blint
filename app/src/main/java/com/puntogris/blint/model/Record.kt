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
    val recordId: Int = 0,

    @ColumnInfo
    val type: String = "",

    @ColumnInfo
    val externalID: Int = 0,

    @ColumnInfo
    val externalName :String = "",

    @ColumnInfo
    val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var amount: Int = 0,

    @ColumnInfo
    val productID: Int = 0,

    @ColumnInfo
    val productName:String = "",

    @ColumnInfo
    val author: String = "",

    @ColumnInfo
    var businessId:String = "",

    @ColumnInfo
    var productUnitPrice: Int = 0,

    @ColumnInfo
    val parentRecordId: String = ""
):Parcelable