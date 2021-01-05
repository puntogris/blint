package com.puntogris.blint.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Client(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)