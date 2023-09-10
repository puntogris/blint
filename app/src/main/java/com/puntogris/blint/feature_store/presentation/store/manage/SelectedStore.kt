package com.puntogris.blint.feature_store.presentation.store.manage

import com.puntogris.blint.feature_store.domain.model.Store

data class SelectedStore(
    val store: Store,
    val isSelected: Boolean
)
