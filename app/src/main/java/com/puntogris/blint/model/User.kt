package com.puntogris.blint.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User(
        @PrimaryKey
        val id:Int = 1,

)