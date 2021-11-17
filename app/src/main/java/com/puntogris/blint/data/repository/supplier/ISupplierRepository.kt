package com.puntogris.blint.data.repository.supplier

import androidx.paging.PagingData
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface ISupplierRepository {
    suspend fun saveSupplierDatabase(supplier: Supplier): SimpleResult
    suspend fun getSupplierPagingDataFlow(): Flow<PagingData<Supplier>>
    suspend fun deleteSupplierDatabase(supplierId: Int): SimpleResult
    suspend fun getSupplierRecordsPagingDataFlow(supplierId: Int): Flow<PagingData<Record>>
    suspend fun getSupplierWithNamePagingDataFlow(name:String): Flow<PagingData<Supplier>>
}