package com.puntogris.blint.model

import androidx.annotation.Keep

@Keep
class FirestoreUser(
    val uid: String = "",
    val country: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val email: String = "",
    val businessesCounter: Int = 0
)