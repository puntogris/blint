package com.puntogris.blint.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirestoreRecord(
    var amount: Int = 0,
    val productId: String = "",
    val productName:String = "",
    var recordId: String = "",
    val value: Float = 0F,
    val sku: String = "",
    val barcode:String = "",
    @get:Exclude var totalInStock: Int = 0,
    @get:Exclude var totalOutStock: Int = 0
    ):Parcelable