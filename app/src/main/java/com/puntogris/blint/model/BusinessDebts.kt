package com.puntogris.blint.model

import androidx.annotation.Keep

@Keep
data class BusinessDebts(
    val clientsDebt: Float = 0f,
    val suppliersDebt: Float = 0f
)