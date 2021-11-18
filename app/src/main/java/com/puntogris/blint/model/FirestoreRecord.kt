package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FirestoreRecord(
    var amount: Int = 0,
    val productId: Int = 0,
    val productName: String = "",
    var recordId: Int = 0,
    val value: Float = 0F,
    val sku: String = "",
    val barcode: String = "",
    var totalInStock: Int = 0,
    var totalOutStock: Int = 0
) : Parcelable