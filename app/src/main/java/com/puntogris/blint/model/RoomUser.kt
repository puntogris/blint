package com.puntogris.blint.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey

@Entity
class RoomUser(
        @PrimaryKey(autoGenerate = false)
        val id:String = "1",

        @ColumnInfo
        val currentUid:String = "",

        @ColumnInfo
        val currentBusinessId:String = "",

        @ColumnInfo
        val currentBusinessType:String = "",

        @ColumnInfo
        val currentBusinessName:String = "",
)