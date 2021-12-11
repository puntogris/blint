package com.puntogris.blint.feature_store.domain.repository

import com.puntogris.blint.common.utils.types.DeleteStore
import com.puntogris.blint.common.utils.types.SimpleProgressResource
import com.puntogris.blint.feature_store.domain.model.Store
import kotlinx.coroutines.flow.Flow

interface StoreRepository {

    fun getStoresFlow(): Flow<List<Store>>

    fun getCurrentStoreFlow(): Flow<Store>

    fun registerStore(businessName: String): Flow<SimpleProgressResource>

    suspend fun deleteStore(businessId: String): DeleteStore
}