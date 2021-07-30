package com.puntogris.blint.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.FirestoreSupplier
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Supplier

@Dao
interface SuppliersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: Supplier)

    @Update
    suspend fun update(supplier: Supplier)

    @Query("DELETE FROM supplier WHERE supplierId = :id")
    suspend fun delete(id: String)

    @Query("SELECT COUNT(*) FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1'")
    fun getCount(): LiveData<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1'")
    fun getAllPaged(): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1' AND companyName LIKE :name")
    fun getPagedSearch(name :String): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1'")
    suspend fun getAllSuppliers(): List<Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN user ON businessId = currentBusinessId WHERE userId = '1' AND companyName LIKE :name LIMIT 30")
    suspend fun getSupplierWithName(name: String): List<Supplier>
}