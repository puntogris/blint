package com.puntogris.blint.feature_store.domain.model

data class CheckableSupplier(
    val supplier: Supplier,
    val isChecked: Boolean
)