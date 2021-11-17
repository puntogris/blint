package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.puntogris.blint.utils.Constants.ADMINISTRATOR
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.Constants.ONLINE
import com.puntogris.blint.utils.IDGenerator
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
@Keep
data class Business(

    @PrimaryKey(autoGenerate = false)
    var businessId: String = IDGenerator.randomID(),

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    val type: String = "",

    @ColumnInfo
    val ownerUid: String = "",

    @ColumnInfo
    val role: String = "",

    @ColumnInfo
    val businessCreatedAt: Timestamp = Timestamp.now(),

    @ColumnInfo
    val email: String = "",

    @ColumnInfo
    val status: String = "",

    @ColumnInfo
    val lastStatusTimestamp: Timestamp = Timestamp.now(),

    @ColumnInfo
    val logoUri: String = ""

) : Parcelable