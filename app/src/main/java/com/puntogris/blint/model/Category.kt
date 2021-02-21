package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId:Int = 0,

    @ColumnInfo
    val name:String,

    @ColumnInfo
    val businessId:String)