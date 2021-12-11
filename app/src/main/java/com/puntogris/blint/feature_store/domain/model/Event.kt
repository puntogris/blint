package com.puntogris.blint.feature_store.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize
import org.threeten.bp.OffsetDateTime

@Parcelize
@Entity
@Keep
data class Event(

    @PrimaryKey(autoGenerate = false)
    var eventId: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    var timestamp: OffsetDateTime = OffsetDateTime.now(),

    @ColumnInfo
    var status: String = Constants.PENDING,

    @ColumnInfo
    var title: String = "",

    @ColumnInfo
    var message: String = "",

    @ColumnInfo
    var storeId: String = ""
) : Parcelable