package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.puntogris.blint.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Business(

    @PrimaryKey(autoGenerate = false)
    val businessId: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    val type: String = "",

    @ColumnInfo
    val ownerUid: String = "",

    @ColumnInfo
    val role: String = "",

    @ColumnInfo
    val createdAt: Timestamp = Timestamp.now(),

    @ColumnInfo
    val email: String = "",

    @ColumnInfo
    val status: String = "",

    @ColumnInfo
    val lastStatusTimestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    val logoUri: String = ""

) : Parcelable