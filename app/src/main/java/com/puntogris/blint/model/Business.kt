package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Business(

        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,

        @ColumnInfo
        val name: String
)