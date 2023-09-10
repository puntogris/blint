package com.puntogris.blint.feature_store.domain.model.order

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.puntogris.blint.common.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize
import org.threeten.bp.OffsetDateTime

@Parcelize
@Entity(tableName = "Orders")
data class Order(

    @PrimaryKey(autoGenerate = false)
    var orderId: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    val timestamp: OffsetDateTime = OffsetDateTime.now(),

    @ColumnInfo
    var total: Float = 0F,

    @ColumnInfo
    var type: String = "IN",

    @ColumnInfo
    var traderId: String = "",

    @ColumnInfo
    var traderName: String = "",

    @ColumnInfo
    var storeId: String = "",

    @ColumnInfo
    var number: Int = 1,

    @ColumnInfo
    var debtId: String = "",

    @ColumnInfo
    var businessName: String = ""

) : Parcelable
