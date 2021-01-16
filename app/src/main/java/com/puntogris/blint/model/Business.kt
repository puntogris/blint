package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Business(

        @PrimaryKey
        var id: String = "",

        @ColumnInfo
        val name: String = "",

        @ColumnInfo
        val type: String = "",

        @ColumnInfo
        val owner: String = "",

        @ColumnInfo
        val userRole:String = "",

        @ColumnInfo
        var userID:String = ""
)
