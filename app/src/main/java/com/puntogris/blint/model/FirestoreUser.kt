package com.puntogris.blint.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
class FirestoreUser(
    val uid:String = "",
    val country: String = "",
    val name: String = "",
    val imageUrl:String = "",
    val email:String = "",
    val businessesCounter: Int = 0
)