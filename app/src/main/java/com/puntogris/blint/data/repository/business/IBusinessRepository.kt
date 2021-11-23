package com.puntogris.blint.data.repository.business

import com.puntogris.blint.utils.types.DeleteBusiness
import com.puntogris.blint.utils.types.SimpleRepoResult
import kotlinx.coroutines.flow.Flow

interface IBusinessRepository {

    fun registerBusiness(businessName: String): Flow<SimpleRepoResult>

    suspend fun deleteBusiness(businessId: String): DeleteBusiness
}