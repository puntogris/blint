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
data class Event(

    @PrimaryKey
    var eventId: String = "",

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
) : Parcelable