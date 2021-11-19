package com.puntogris.blint.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.data.data_source.toProductCategoryCrossRef
import com.puntogris.blint.data.data_source.toProductSupplierCrossRef
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.model.product.ProductCategoryCrossRef
import com.puntogris.blint.model.product.ProductSupplierCrossRef
import com.puntogris.blint.model.product.ProductWithDetails

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Transaction
    suspend fun insertProduct(productWithDetails: ProductWithDetails) {
        insert(productWithDetails.product)
        renewProductSuppliers(productWithDetails)
        renewProductCategories(productWithDetails)
    }

    @Query("DELETE FROM product WHERE productId = :productId")
    suspend fun delete(productId: Int)

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND barcode = :barcode")
    suspend fun getProductWithBarcode(barcode: String): ProductWithDetails?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' ORDER BY name ASC")
    fun getProductsPaged(): PagingSource<Int, ProductWithDetails>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product p INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND p.name LIKE :query OR barcode LIKE :query OR sku LIKE :query")
    fun getProductsWithQueryPaged(query: String): PagingSource<Int, ProductWithDetails>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product p INNER JOIN user ON businessId = currentBusinessId WHERE localReferenceId = '1' AND p.name LIKE :query OR barcode LIKE :query OR sku LIKE :query LIMIT 5")
    suspend fun getProductsWithQuery(query: String): List<Product>

    @Query("UPDATE product SET amount = CASE WHEN :type = 'IN' THEN amount + :amount ELSE amount - :amount END WHERE productId = :id")
    suspend fun updateProductAmountWithType(id: String, amount: Int, type: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductSupplierCrossRef(refs: List<ProductSupplierCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductCategoriesCrossRef(refs: List<ProductCategoryCrossRef>)

    @Delete
    suspend fun deleteProductCategoriesCrossRef(refs: List<ProductCategoryCrossRef>)

    @Delete
    suspend fun deleteProductSupplierCrossRef(refs: List<ProductSupplierCrossRef>)

    @Query("SELECT * FROM productcategorycrossref WHERE productId = :productId")
    suspend fun getProductCategoriesCrossRefs(productId: Int): List<ProductCategoryCrossRef>

    @Query("SELECT * FROM productsuppliercrossref WHERE productId = :productId")
    suspend fun getProductSupplierCrossRefs(productId: Int): List<ProductSupplierCrossRef>

    @Transaction
    suspend fun renewProductCategories(productWithDetails: ProductWithDetails) {
        val oldRefs = getProductCategoriesCrossRefs(productWithDetails.product.productId)
        val newRefs = productWithDetails.toProductCategoryCrossRef()
        val deleteDif = oldRefs.filterNot { newRefs.contains(it) }
        val insertDif = newRefs.filterNot { oldRefs.contains(it) }
        deleteProductCategoriesCrossRef(deleteDif)
        insertProductCategoriesCrossRef(insertDif)
    }

    @Transaction
    suspend fun renewProductSuppliers(productWithDetails: ProductWithDetails) {
        val oldRefs = getProductSupplierCrossRefs(productWithDetails.product.productId)
        val newRefs = productWithDetails.toProductSupplierCrossRef()
        val deleteDif = oldRefs.filterNot { newRefs.contains(it) }
        val insertDif = newRefs.filterNot { oldRefs.contains(it) }
        deleteProductSupplierCrossRef(deleteDif)
        insertProductSupplierCrossRef(insertDif)
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
                    JOIN user u ON p.businessId = u.currentBusinessId 
                    JOIN productsuppliercrossref r ON r.productId = p.productId
                    WHERE u.localReferenceId = '1' and r.supplierId = :supplierId)
            """
    )
    suspend fun updateSupplierProductsPrices(
        newPrice: Float,
        supplierId: Int,
        valueType: String,
        isValueUp: Boolean,
        affectsBuyPrice: Boolean,
        affectsSellPrice: Boolean,
        affectsSuggestedPrice: Boolean
    ): Int
}