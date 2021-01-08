package com.puntogris.blint.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FirestoreUser(

    @PrimaryKey
    val uid:String,
    val name: String,
    val imageUrl:String,
    val email:String
)