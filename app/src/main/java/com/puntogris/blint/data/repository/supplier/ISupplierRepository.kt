package com.puntogris.blint.data.repository.supplier

import androidx.paging.PagingData
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface ISupplierRepository {
    suspend fun saveSupplierDatabase(supplier: Supplier): SimpleResult

    fun getAllSuppliersPaged(): Flow<PagingData<Supplier>>

    suspend fun deleteSupplierDatabase(supplierId: Int): SimpleResult

    fun getAllSuppliersRecordsPaged(supplierId: Int): Flow<PagingData<Record>>

    fun getSuppliersWithNamePaged(name: String): Flow<PagingData<Supplier>>
}