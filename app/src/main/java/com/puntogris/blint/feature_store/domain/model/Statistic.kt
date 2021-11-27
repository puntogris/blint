package com.puntogris.blint.feature_store.domain.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.puntogris.blint.common.utils.UUIDGenerator

@Entity
@Keep
data class Statistic(

    @PrimaryKey(autoGenerate = false)
    val statisticId: String = UUIDGenerator.randomUUID(),

    @ColumnInfo
    val totalProducts: Int = 0,

    @ColumnInfo
    val totalClients: Int = 0,

    @ColumnInfo
    val totalSuppliers: Int = 0,

    @ColumnInfo
    val totalOrders: Int = 0,

    @ColumnInfo
    val businessId: String = "",

    @ColumnInfo
    val clientsDebt: Float = 0F,

    @ColumnInfo
    val suppliersDebt: Float = 0F
)