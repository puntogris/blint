package com.puntogris.blint.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirestoreSupplier(
    val companyName:String = "",
    val supplierId: String = ""
    ):Parcelable