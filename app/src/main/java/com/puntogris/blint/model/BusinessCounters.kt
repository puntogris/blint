package com.puntogris.blint.model

import androidx.annotation.Keep

@Keep
data class BusinessCounters(
    val totalProducts: Int = 0,
    val totalClients: Int = 0,
    val totalSuppliers: Int = 0
)