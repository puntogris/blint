package com.puntogris.blint.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Supplier

@Dao
interface SuppliersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: Supplier)

    @Update
    suspend fun update(supplier: Supplier)

    @Query("DELETE FROM supplier WHERE supplierId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM supplier WHERE supplierId = :id")
    suspend fun getSupplier(id: Int): Supplier

    @Query("SELECT COUNT(*) FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    suspend fun getCount(): Int

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    fun getAllPaged(): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1' AND companyName LIKE :name")
    fun getPagedSearch(name :String): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    suspend fun getAllSuppliers(): List<Supplier>

}