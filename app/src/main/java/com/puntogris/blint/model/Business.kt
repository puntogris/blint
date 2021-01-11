package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Business(

        @PrimaryKey
        var id: String = "",

        @ColumnInfo
        val name: String = "",

        @ColumnInfo
        val type: String = "",

        @ColumnInfo
        val owner: String = ""
)