package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FirestoreDebt(
    var debtId: Int = 0,
    var type: String = "",
    var amount: Float = 0f
) : Parcelable