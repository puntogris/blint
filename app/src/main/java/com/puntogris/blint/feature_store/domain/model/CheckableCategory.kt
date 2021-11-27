package com.puntogris.blint.feature_store.domain.model

data class CheckableCategory(
    val category: Category,
    val isChecked: Boolean
)