package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FirestoreDebt(
    var debtId: String = "",
    var type: String = "",
    var amount: Float = 0f
):Parcelable