package com.puntogris.blint.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductSupplierCrossRef
import com.puntogris.blint.model.ProductWithSuppliers

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Query("DELETE FROM product WHERE productId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM product WHERE productId = :id ")
    suspend fun getProduct(id: Int): Product

    @Query("SELECT * FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1' AND barcode = :barcode")
    suspend fun getProductWithBarcode(barcode: String): Product?

    @Query("SELECT COUNT(*) FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1'")
    suspend fun getCount(): Int

    @Query("SELECT * FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1' ORDER BY name ASC")
    fun getAllPaged(): PagingSource<Int, Product>

    @Query("SELECT * FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE id = '1' AND name LIKE :name")
    fun getPagedSearch(name: String): PagingSource<Int, Product>

    @Query("UPDATE product SET amount = :amount WHERE productId = :id")
    suspend fun updateProductAmount(id: Int, amount: Int)

    @Query("UPDATE product SET image = :empty")
    suspend fun clearImages(empty: HashMap<String, String>)

    @Transaction
    @Query("SELECT * FROM product WHERE productId = :id")
    suspend fun getProductWithSuppliers(id: Int): ProductWithSuppliers

    @Insert
    suspend fun insertProductSupplierCrossRef(items: List<ProductSupplierCrossRef>)


    @Query(
        """
            UPDATE product
                SET buyPrice = CASE
                    WHEN :affectsBuyPrice 
                        THEN CASE
                            WHEN :valueType = '%' 
                            THEN CASE 
                                WHEN :isValueUp 
                                THEN (buyPrice + ((buyPrice * :newPrice) / 100)) 
                                ELSE (buyPrice - ((buyPrice * :newPrice) / 100)) 
                                END
                            ELSE CASE
                                WHEN :isValueUp 
                                THEN buyPrice + :newPrice 
                                ELSE buyPrice - :newPrice 
                                END
                            END
                        ELSE buyPrice END, 
                    suggestedSellPrice = CASE
                    WHEN :affectsSuggestedPrice
                        THEN CASE
                            WHEN :valueType = '%' 
                            THEN CASE
                                WHEN :isValueUp 
                                THEN (suggestedSellPrice + ((suggestedSellPrice * :newPrice) / 100)) 
                                ELSE (suggestedSellPrice - ((suggestedSellPrice * :newPrice) / 100)) 
                                END
                            ELSE CASE 
                                WHEN :isValueUp 
                                THEN suggestedSellPrice + :newPrice 
                                ELSE suggestedSellPrice - :newPrice 
                                END
                            END
                        ELSE sellPrice END,
                    sellPrice = CASE
                    WHEN :affectsSellPrice
                        THEN CASE
                            WHEN :valueType = '%' 
                            THEN CASE 
                                WHEN :isValueUp 
                                THEN (sellPrice + ((sellPrice * :newPrice) / 100)) 
                                ELSE (sellPrice - ((sellPrice * :newPrice) / 100)) 
                                END
                            ELSE CASE 
                                WHEN :isValueUp 
                                THEN sellPrice + :newPrice 
                                ELSE sellPrice - :newPrice 
                                END
                            END
                        ELSE sellPrice END
                WHERE productId IN 
                    (SELECT p.productId 
                    FROM product p 
                    JOIN roomuser u ON p.businessId = u.currentBusinessId 
                    JOIN productsuppliercrossref r ON r.productId = p.productId
                    WHERE u.id = '1' and r.supplierId = :supplierId)
            """
    )
    suspend fun updateSupplierProductsPrices(
        newPrice: Float,
        supplierId: Int,
        valueType: String,
        isValueUp: Boolean,
        affectsBuyPrice : Boolean,
        affectsSellPrice:Boolean,
        affectsSuggestedPrice:Boolean
    )
}