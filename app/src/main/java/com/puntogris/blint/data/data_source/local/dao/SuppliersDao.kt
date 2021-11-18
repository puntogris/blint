package com.puntogris.blint.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Supplier

@Dao
interface SuppliersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: Supplier)

    @Query("DELETE FROM supplier WHERE supplierId = :supplierId")
    suspend fun delete(supplierId: Int)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    fun getAllPaged(): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND companyName LIKE :query")
    fun getPagedSearch(query: String): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1'")
    suspend fun getAllSuppliers(): List<Supplier>
}