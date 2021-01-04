package com.puntogris.blint.data.local.product

import androidx.room.*
import com.puntogris.blint.model.Product

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

}