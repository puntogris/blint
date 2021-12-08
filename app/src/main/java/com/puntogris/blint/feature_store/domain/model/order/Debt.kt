package com.puntogris.blint.feature_store.domain.model.order

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.puntogris.blint.common.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize
import org.threeten.bp.OffsetDateTime

@Parcelize
@Entity
@Keep
data class Debt(

    @PrimaryKey(autoGenerate = false)
    var debtId: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    var amount: Float = 0F,

    @ColumnInfo
    var orderId: String = "",

    @ColumnInfo
    var traderId: String = "",

    @ColumnInfo
    var traderName: String = "",

    @ColumnInfo
    var timestamp: OffsetDateTime = OffsetDateTime.now(),

    @ColumnInfo
    var businessId: String = "",

    @ColumnInfo
    var traderType: String = ""
) : Parcelable