package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.feature_store.domain.model.Supplier

@Dao
interface SuppliersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: Supplier)

    @Query("DELETE FROM supplier WHERE supplierId = :supplierId")
    suspend fun delete(supplierId: String)

    @Query("SELECT * FROM supplier WHERE supplierId = :supplierId")
    suspend fun getSupplier(supplierId: String): Supplier

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getSuppliers(): List<Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    fun getSuppliersPaged(): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND companyName LIKE ('%'|| :query ||'%')")
    fun getSuppliersSearchPaged(query: String): PagingSource<Int, Supplier>

    @Query("UPDATE supplier SET debt = debt + :amount WHERE supplierId = :supplierId")
    suspend fun updateSupplierDebt(supplierId: String, amount: Float)
}