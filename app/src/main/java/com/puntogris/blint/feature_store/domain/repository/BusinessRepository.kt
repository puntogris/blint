package com.puntogris.blint.feature_store.domain.repository

import com.puntogris.blint.common.utils.types.DeleteBusiness
import com.puntogris.blint.common.utils.types.SimpleRepoResult
import com.puntogris.blint.feature_store.domain.model.Business
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {

    fun getBusinessFlow(): Flow<List<Business>>

    fun registerBusiness(businessName: String): Flow<SimpleRepoResult>

    suspend fun deleteBusiness(businessId: String): DeleteBusiness
}