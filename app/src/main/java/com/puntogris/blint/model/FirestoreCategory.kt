package com.puntogris.blint.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirestoreCategory(
    val name: String = "",
    val categoryId: String = ""
    ):Parcelable