package com.puntogris.blint.data.repo.supplier

import androidx.paging.PagingData
import com.puntogris.blint.model.FirestoreSupplier
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface ISupplierRepository {
    suspend fun saveSupplierDatabase(supplier: Supplier): SimpleResult
    suspend fun getSupplierPagingDataFlow(): Flow<PagingData<Supplier>>
    suspend fun deleteSupplierDatabase(supplierId: String): SimpleResult
    suspend fun getSupplierRecordsPagingDataFlow(supplierId: String): Flow<PagingData<Record>>
    suspend fun getSupplierWithNamePagingDataFlow(name:String): Flow<PagingData<Supplier>>
}