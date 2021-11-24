package com.puntogris.blint.domain.repository

import com.puntogris.blint.model.Business
import com.puntogris.blint.utils.types.DeleteBusiness
import com.puntogris.blint.utils.types.SimpleRepoResult
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {

    suspend fun getBusinessListRoom(): List<Business>

    fun registerBusiness(businessName: String): Flow<SimpleRepoResult>

    suspend fun deleteBusiness(businessId: String): DeleteBusiness
}