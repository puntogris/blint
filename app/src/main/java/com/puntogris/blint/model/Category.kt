package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey
    val categoryId: String = "",

    @ColumnInfo
    val name:String,

    @ColumnInfo
    val businessId:String)