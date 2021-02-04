package com.puntogris.blint.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Event (

    @PrimaryKey(autoGenerate = true)
    val eventId: Int = 0,

    @ColumnInfo
    var timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    var status: String = "PENDING",

    @ColumnInfo
    var title: String = "",

    @ColumnInfo
    var message: String = "",

    @ColumnInfo
    var businessId: String = ""
):Parcelable