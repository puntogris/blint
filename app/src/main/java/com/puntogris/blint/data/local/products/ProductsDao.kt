package com.puntogris.blint.data.local.products

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Product
import java.util.concurrent.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Query("SELECT COUNT(*) FROM product")
    suspend fun getCount(): Int

    @Query("SELECT * FROM product")
    fun getAllPaged(): PagingSource<Int, Product>

}