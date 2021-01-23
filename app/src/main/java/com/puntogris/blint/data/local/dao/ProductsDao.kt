package com.puntogris.blint.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductSupplierCrossRef
import com.puntogris.blint.model.ProductWithSuppliers

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product):Long

    @Update
    suspend fun update(product: Product)

    @Query("DELETE FROM product WHERE productId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM product WHERE productId = :id")
    suspend fun getProduct(id: Int): Product

    @Query("SELECT * FROM product WHERE barcode = :barcode")
    suspend fun getProductWithBarcode(barcode: String): Product?

    @Query("SELECT COUNT(*) FROM product")
    suspend fun getCount(): Int

    @Query("SELECT * FROM product ORDER BY name ASC")
    fun getAllPaged(): PagingSource<Int, Product>

    @Query("SELECT * FROM product WHERE name LIKE :name")
    fun getPagedSearch(name: String): PagingSource<Int, Product>

    @Query("UPDATE product SET amount = :amount WHERE productId = :id")
    suspend fun updateProductAmount(id: Int, amount: Int)

    @Query("UPDATE product SET image = :empty")
    suspend fun clearImages(empty: HashMap<String, String>)

    @Transaction
    @Query("SELECT * FROM product WHERE productId = :id")
    suspend fun getProductWithSuppliers(id:Int):ProductWithSuppliers

    @Insert
    suspend fun insertProductSupplierCrossRef(items:List<ProductSupplierCrossRef>)

//    @Query("SELECT * FROM product ORDER BY CASE WHEN :isAsc = 1 THEN name END ASC, CASE WHEN :isAsc = 0 THEN name END DESC")
//    fun getAllPaged(isAsc:Boolean): PagingSource<Int, Product>

}