package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.model.order.Record
import kotlinx.coroutines.flow.Flow

interface SupplierRepository {

    suspend fun getSuppliers(): List<Supplier>

    suspend fun getSupplier(supplierId: String): Supplier

    suspend fun saveSupplier(supplier: Supplier): SimpleResource

    suspend fun deleteSupplier(supplierId: String): SimpleResource

    fun getSuppliersPaged(query: String? = null): Flow<PagingData<Supplier>>

    fun getSupplierRecordsPaged(supplierId: String): Flow<PagingData<Record>>
}