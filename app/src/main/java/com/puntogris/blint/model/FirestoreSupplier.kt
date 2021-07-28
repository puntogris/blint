package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class FirestoreSupplier(
    val companyName:String = "",
    val supplierId: String = ""
    ):Parcelable