package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Statistic(

    @PrimaryKey(autoGenerate = true)
    val statisticId: Int = 0,

    @ColumnInfo
    val totalProducts :Int = 0,

    @ColumnInfo
    val totalClients : Int = 0,

    @ColumnInfo
    val totalSuppliers : Int = 0,

    @ColumnInfo
    val businessId: String = ""
)