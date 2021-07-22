package com.puntogris.blint.model

import androidx.room.Entity
import androidx.room.PrimaryKey

class FirestoreUser(

    val uid:String,
    val name: String,
    val imageUrl:String,
    val email:String,
    val businessesCounter: Int = 0
)