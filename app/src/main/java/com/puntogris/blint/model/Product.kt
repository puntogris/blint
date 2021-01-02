package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    val barcode: String = "",

    @ColumnInfo
    val description: String = "",

    @ColumnInfo
    val amount: Int = 0,

    @ColumnInfo
    val image: String = ""
)