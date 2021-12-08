package com.puntogris.blint.feature_store.domain.model.product

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import org.threeten.bp.OffsetDateTime

@Entity
@Parcelize
@IgnoreExtraProperties
@Keep
data class Product(

    @PrimaryKey(autoGenerate = false)
    var productId: String = "",

    @ColumnInfo
    var name: String = "",

    @ColumnInfo
    var barcode: String = "",

    @ColumnInfo
    var description: String = "",

    @ColumnInfo
    var amount: Int = 0,

    @ColumnInfo
    var image: String = "",

    @ColumnInfo
    var sellPrice: Float = 0F,

    @ColumnInfo
    var buyPrice: Float = 0F,

    @ColumnInfo
    var suggestedSellPrice: Float = 0F,

    @ColumnInfo
    var sku: String = "",

    @ColumnInfo
    var brand: String = "",

    @ColumnInfo
    var size: String = "",

    @ColumnInfo
    var lastRecordTimestamp: OffsetDateTime = OffsetDateTime.now(),

    @ColumnInfo
    var historicInStock: Int = 0,

    @ColumnInfo
    var historicOutStock: Int = 0,

    @ColumnInfo
    var businessId: String = "",

    @ColumnInfo
    val minStock: Int = 0

) : Parcelable