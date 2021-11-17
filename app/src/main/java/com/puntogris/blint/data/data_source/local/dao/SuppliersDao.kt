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
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1'")
    fun getAllPaged(): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1' AND companyName LIKE :name")
    fun getPagedSearch(name :String): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1'")
    suspend fun getAllSuppliers(): List<Supplier>
}