package com.puntogris.blint.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity
@Keep
data class Statistic(

    @PrimaryKey(autoGenerate = true)
    val statisticId: Int = 0,

    @ColumnInfo
    val totalProducts: Int = 0,

    @ColumnInfo
    val totalClients: Int = 0,

    @ColumnInfo
    val totalSuppliers: Int = 0,

    @ColumnInfo
    val totalOrders: Int = 0,

    @ColumnInfo
    val businessId: Int = 0,

    @ColumnInfo
    val clientsDebt: Float = 0F,

    @ColumnInfo
    val suppliersDebt: Float = 0F
)