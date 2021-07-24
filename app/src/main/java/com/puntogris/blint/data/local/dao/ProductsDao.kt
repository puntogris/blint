package com.puntogris.blint.data.local.dao

import android.app.Service
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
        insert(product.product)
        val productId = product.product.productId
        //if (isNewProduct) statisticsDao.incrementTotalProducts()

        product.suppliers?.map {
            ProductSupplierCrossRef(productId, it.supplierId)
        }?.let {
            renewProductSuppliers(it, productId)
        }
        product.categories?.map {
            ProductCategoryCrossRef(productId, it.categoryName)
        }?.let {
            renewProductCategories(it, productId)
         //   insertProductCategoriesCrossRef(it)
        }
       // if (product.product.amount != 0) ordersDao.insert(record)

    }

    @Update
    suspend fun update(product: Product)

    @Query("DELETE FROM product WHERE productId = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM product WHERE productId = :id ")
    suspend fun getProduct(id: String): Product

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN roomuser ON businessId = currentBusinessId WHERE userId = '1' AND barcode = :barcode")
    suspend fun getProductWithBarcode(barcode: String): ProductWithSuppliersCategories?

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductSupplierCrossRef(items: List<ProductSupplierCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductCategoriesCrossRef(items: List<ProductCategoryCrossRef>)

    @Query("SELECT * FROM productcategorycrossref WHERE productId = :productId")
    suspend fun getProductCategoriesCrossRefs(productId:String): List<ProductCategoryCrossRef>

    @Query("SELECT * FROM productsuppliercrossref WHERE productId = :productId")
    suspend fun getProductSupplierCrossRefs(productId:String): List<ProductSupplierCrossRef>

    @Delete
    suspend fun deleteProductCategoriesCrossRef(productCategoryCrossRef: ProductCategoryCrossRef)

    @Delete
    suspend fun deleteProductSupplierCrossRef(productCategoryCrossRef: ProductSupplierCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductCategoriesCrossRef(items: ProductCategoryCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductSupplierCrossRef(items: ProductSupplierCrossRef)

    @Transaction
    suspend fun renewProductCategories(items: List<ProductCategoryCrossRef> ,productId: String){
        val oldRefs = getProductCategoriesCrossRefs(productId)
        if (oldRefs.isNullOrEmpty()) insertProductCategoriesCrossRef(items)
        else{
            oldRefs.forEach {
                if (!items.contains(it)) deleteProductCategoriesCrossRef(it)
                else insertProductCategoriesCrossRef(it)
            }
        }
    }

    @Transaction
    suspend fun renewProductSuppliers(items: List<ProductSupplierCrossRef>, productId: String){
        val oldRefs = getProductSupplierCrossRefs(productId)
        if (oldRefs.isNullOrEmpty()) insertProductSupplierCrossRef(items)
        else{
            oldRefs.forEach {
                if (!items.contains(it)) deleteProductSupplierCrossRef(it)
                else insertProductSupplierCrossRef(it)
            }
        }
    }

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