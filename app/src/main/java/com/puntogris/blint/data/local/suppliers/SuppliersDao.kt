package com.puntogris.blint.data.local.suppliers

import androidx.room.*
import com.puntogris.blint.model.Supplier

@Dao
interface SuppliersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: Supplier)

    @Update
    suspend fun update(supplier: Supplier)

    @Query("SELECT * FROM supplier")
    suspend fun getSuppliers(): List<Supplier>

    @Query("SELECT COUNT(*) FROM supplier")
    suspend fun getCount(): Int

}