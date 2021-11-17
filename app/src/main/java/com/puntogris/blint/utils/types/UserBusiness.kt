package com.puntogris.blint.utils.types

import com.puntogris.blint.model.Business

sealed class UserBusiness() {
    class Success(val data: List<Business>) : UserBusiness()
    class Error(val exception: Exception) : UserBusiness()
    object NotFound : UserBusiness()
    object InProgress : UserBusiness()
}
