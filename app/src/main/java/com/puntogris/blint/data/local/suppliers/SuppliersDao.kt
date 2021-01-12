package com.puntogris.blint.data.local.suppliers

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

    @Query("DELETE FROM supplier WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM supplier WHERE id = :id")
    suspend fun getSupplier(id: Int): Supplier

    @Query("SELECT COUNT(*) FROM supplier")
    suspend fun getCount(): Int

    @Query("SELECT * FROM supplier")
    fun getAllPaged(): PagingSource<Int, Supplier>

    @Query("SELECT * FROM supplier WHERE companyName LIKE :name")
    fun getPagedSearch(name :String): PagingSource<Int, Supplier>


}