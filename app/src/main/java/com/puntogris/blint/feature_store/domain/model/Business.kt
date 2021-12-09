package com.puntogris.blint.feature_store.domain.model

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
data class Business(

    @PrimaryKey(autoGenerate = false)
    val businessId: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    val ownerUid: String = "",

    @ColumnInfo
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @ColumnInfo
    val logoUri: String = "",

    @ColumnInfo
    val totalProducts: Int = 0,

    @ColumnInfo
    val totalClients: Int = 0,

    @ColumnInfo
    val totalSuppliers: Int = 0,

    @ColumnInfo
    val totalOrders: Int = 0,

    @ColumnInfo
    val clientsDebt: Float = 0F,

    @ColumnInfo
    val suppliersDebt: Float = 0F

) : Parcelable
