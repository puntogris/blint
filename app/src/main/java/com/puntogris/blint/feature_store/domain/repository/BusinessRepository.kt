package com.puntogris.blint.feature_store.domain.repository

import com.puntogris.blint.common.utils.types.DeleteBusiness
import com.puntogris.blint.common.utils.types.SimpleProgressResource
import com.puntogris.blint.feature_store.domain.model.Business
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {

    fun getBusinessesFlow(): Flow<List<Business>>

    fun getCurrentBusinessFlow(): Flow<Business>

    fun registerBusiness(businessName: String): Flow<SimpleProgressResource>

    suspend fun deleteBusiness(businessId: String): DeleteBusiness
}