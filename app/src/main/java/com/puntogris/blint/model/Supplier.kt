package com.puntogris.blint.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Supplier(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)