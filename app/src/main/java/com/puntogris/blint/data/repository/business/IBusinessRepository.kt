package com.puntogris.blint.data.repository.business

import com.puntogris.blint.utils.types.DeleteBusiness
import com.puntogris.blint.utils.types.SimpleResult

interface IBusinessRepository {

    suspend fun registerLocalBusiness(businessName: String): SimpleResult

    suspend fun deleteBusinessDatabase(businessId: Int): DeleteBusiness
}