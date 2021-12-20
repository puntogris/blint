package com.puntogris.blint.feature_store.domain.model.order

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.puntogris.blint.common.utils.UUIDGenerator
import kotlinx.parcelize.Parcelize
import org.threeten.bp.OffsetDateTime

@Parcelize
@Entity
@Keep
data class Record(

    @PrimaryKey(autoGenerate = false)
    var recordId: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    var type: String = "",

    @ColumnInfo
    var traderId: String = "",

    @ColumnInfo
    var traderName: String = "",

    @ColumnInfo
    val timestamp: OffsetDateTime = OffsetDateTime.now(),

    @ColumnInfo
    var amount: Int = 0,

    @ColumnInfo
    var productId: String = "",

    @ColumnInfo
    var productName: String = "",

    @ColumnInfo
    var productImage: String = "",

    @ColumnInfo
    var storeId: String = "",

    @ColumnInfo
    var productUnitPrice: Float = 0F,

    @ColumnInfo
    var orderId: String = "",

    @ColumnInfo
    var total: Float = 0F,

    @ColumnInfo
    var historicInStock: Int = 0,

    @ColumnInfo
    var historicOutStock: Int = 0,

    @ColumnInfo
    var sku: String = "",

    @ColumnInfo
    var barcode: String = ""
) : Parcelable
