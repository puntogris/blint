package com.puntogris.blint.data.repository.supplier

import androidx.paging.PagingData
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface ISupplierRepository {

    suspend fun saveSupplier(supplier: Supplier): SimpleResult

    suspend fun deleteSupplier(supplierId: Int): SimpleResult

    fun getSuppliersPaged(): Flow<PagingData<Supplier>>

    fun getSupplierRecordsPaged(supplierId: Int): Flow<PagingData<Record>>

    fun getSuppliersWithQueryPaged(query: String): Flow<PagingData<Supplier>>
}