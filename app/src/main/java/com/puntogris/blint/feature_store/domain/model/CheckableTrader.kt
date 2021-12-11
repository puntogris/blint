package com.puntogris.blint.feature_store.domain.model

data class CheckableTrader(
    val trader: Trader,
    val isChecked: Boolean
)