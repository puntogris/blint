package com.puntogris.blint.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RoomUser(
        @PrimaryKey
        val id:Int = 1,
)