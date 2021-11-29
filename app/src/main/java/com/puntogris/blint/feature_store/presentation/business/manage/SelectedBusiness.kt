package com.puntogris.blint.feature_store.presentation.business.manage

import com.puntogris.blint.feature_store.domain.model.Business

data class SelectedBusiness(
    val business: Business,
    val isSelected: Boolean
)