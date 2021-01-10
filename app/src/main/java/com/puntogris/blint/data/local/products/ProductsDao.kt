package com.puntogris.blint.data.local.products

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Product

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT COUNT(*) FROM product")
    suspend fun getCount(): Int

    @Query("SELECT * FROM product ORDER BY name ASC")
    fun getAllPaged(): PagingSource<Int, Product>

//    @Query("SELECT * FROM product ORDER BY CASE WHEN :isAsc = 1 THEN name END ASC, CASE WHEN :isAsc = 0 THEN name END DESC")
//    fun getAllPaged(isAsc:Boolean): PagingSource<Int, Product>

}