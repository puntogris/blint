package com.puntogris.blint.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductCategoryCrossRef
import com.puntogris.blint.model.ProductSupplierCrossRef
import com.puntogris.blint.model.ProductWithSuppliersCategories

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Transaction
    suspend fun insertProduct(product: ProductWithSuppliersCategories){
      //  println(product.product)
        insert(product.product)

        //if (isNewProduct) statisticsDao.incrementTotalProducts()

        product.suppliers?.map {
            ProductSupplierCrossRef(product.product.productId, it.supplierId)
        }?.let {
            insertProductSupplierCrossRef(it)
        }
        product.categories?.map {
            ProductCategoryCrossRef(product.product.productId, it.categoryId)
        }?.let {
            insertProductCategoriesCrossRef(it)
        }
       // if (product.product.amount != 0) ordersDao.insert(record)
//
    }

    @Update
    suspend fun update(product: Product)

    @Query("DELETE FROM product WHERE productId = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM product WHERE productId = :id ")
    suspend fun getProduct(id: String): Product

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND barcode = :barcode")
    suspend fun getProductWithBarcode(barcode: String): Product?

    @Query("SELECT COUNT(*) FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1'")
    fun getCount(): LiveData<Int>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' ORDER BY name ASC")
    fun getAllPaged(): PagingSource<Int, ProductWithSuppliersCategories>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND name LIKE :text OR barcode LIKE :text OR sku LIKE :text")
    fun getPagedSearch(text: String): PagingSource<Int, ProductWithSuppliersCategories>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND name LIKE :name LIMIT 30")
    suspend fun getProductsWithName(name: String): List<Product>

    @Query("UPDATE product SET amount = :amount WHERE productId = :id")
    suspend fun updateProductAmount(id: String, amount: Int)

    @Query("UPDATE product SET amount = CASE WHEN :type = 'IN' THEN amount + :amount ELSE amount - :amount END WHERE productId = :id")
    suspend fun updateProductAmountWithType(id: String, amount: Int, type: String)

    @Transaction
    @Query("SELECT * FROM product WHERE productId = :id")
    suspend fun getProductWithSuppliersCategories(id: String): ProductWithSuppliersCategories

    @Insert
    suspend fun insertProductSupplierCrossRef(items: List<ProductSupplierCrossRef>)

    @Insert
    suspend fun insertProductCategoriesCrossRef(items: List<ProductCategoryCrossRef>)

    @Transaction
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
                    WHERE u.userId = '1' and r.supplierId = :supplierId)
            """
    )
    suspend fun updateSupplierProductsPrices(
        newPrice: Float,
        supplierId: String,
        valueType: String,
        isValueUp: Boolean,
        affectsBuyPrice : Boolean,
        affectsSellPrice:Boolean,
        affectsSuggestedPrice:Boolean
    ):Int
}