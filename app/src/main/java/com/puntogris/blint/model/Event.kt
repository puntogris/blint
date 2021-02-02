package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity
data class Event (

    @PrimaryKey(autoGenerate = true)
    val eventId: Int = 0,

    @ColumnInfo
    val timestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    val status: String = "PENDING",

    @ColumnInfo
    val title: String = "",

    @ColumnInfo
    val message: String = "",

    @ColumnInfo
    val businessId: String = ""
)