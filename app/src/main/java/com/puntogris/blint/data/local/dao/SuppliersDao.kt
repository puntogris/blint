package com.puntogris.blint.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
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

    @Query("SELECT * FROM supplier WHERE supplierId = :id")
    suspend fun getSupplier(id: String): Supplier

    @Query("SELECT COUNT(*) FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    fun getCount(): LiveData<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    fun getAllPaged(): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND companyName LIKE :name")
    fun getPagedSearch(name :String): PagingSource<Int, Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    suspend fun getAllSuppliers(): List<Supplier>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM supplier INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND companyName LIKE :name LIMIT 30")
    suspend fun getSupplierWithName(name: String): List<Supplier>

    @Query("UPDATE supplier SET debt = debt + :amount WHERE supplierId = :supplierId")
    suspend fun updateSupplierDebt(supplierId: String, amount: Float)

}