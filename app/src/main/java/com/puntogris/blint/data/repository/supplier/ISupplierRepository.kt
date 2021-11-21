package com.puntogris.blint.data.repository.supplier

import androidx.paging.PagingData
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface ISupplierRepository {

    suspend fun saveSupplier(supplier: Supplier): SimpleResult

    suspend fun deleteSupplier(supplierId: String): SimpleResult

    fun getSuppliersPaged(query: String? = null): Flow<PagingData<Supplier>>

    fun getSupplierRecordsPaged(supplierId: String): Flow<PagingData<Record>>
}