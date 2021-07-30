package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(

        @PrimaryKey(autoGenerate = false)
        val userId:String = "1",

        @ColumnInfo
        var country: String = "",

        @ColumnInfo
        var username:String = "",

        @ColumnInfo
        val currentUid:String = "",

        @ColumnInfo
        val currentBusinessId:String = ""
)