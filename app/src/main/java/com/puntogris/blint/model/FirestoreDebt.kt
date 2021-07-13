package com.puntogris.blint.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirestoreDebt(
    var debtId: String = "",
    var type: String = "",
    var amount: Float = 0f
):Parcelable