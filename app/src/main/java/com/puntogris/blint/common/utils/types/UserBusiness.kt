package com.puntogris.blint.common.utils.types

import com.puntogris.blint.feature_store.domain.model.Business

sealed class UserBusiness() {
    class Success(val data: List<Business>) : UserBusiness()
    class Error(val exception: Exception) : UserBusiness()
    object NotFound : UserBusiness()
    object InProgress : UserBusiness()
}
