package com.puntogris.blint.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirestoreRecord(
    var amount: Int = 0,
    val productId: String = "",
    val productName:String = "",
    val recordId: String = "",
    val value: Float = 0F
    ):Parcelable