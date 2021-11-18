package com.puntogris.blint.data.repository.business

import com.puntogris.blint.utils.types.DeleteBusiness
import com.puntogris.blint.utils.types.SimpleResult

interface IBusinessRepository {

    suspend fun registerBusiness(businessName: String): SimpleResult

    suspend fun deleteBusiness(businessId: Int): DeleteBusiness
}