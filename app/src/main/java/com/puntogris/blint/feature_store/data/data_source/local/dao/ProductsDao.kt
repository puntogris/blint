package com.puntogris.blint.feature_store.data.data_source.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.puntogris.blint.feature_store.data.data_source.toProductCategoryCrossRef
import com.puntogris.blint.feature_store.data.data_source.toProductSupplierCrossRef
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductCategoryCrossRef
import com.puntogris.blint.feature_store.domain.model.product.ProductSupplierCrossRef
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Query("SELECT * FROM product")
    suspend fun getProducts(): List<Product>

    @Transaction
    suspend fun insertProduct(productWithDetails: ProductWithDetails) {
        insert(productWithDetails.product)
        renewProductSuppliers(productWithDetails)
        renewProductCategories(productWithDetails)
    }

    @Query("DELETE FROM product WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' AND barcode = :barcode")
    suspend fun getProductWithBarcode(barcode: String): ProductWithDetails?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' ORDER BY name ASC")
    fun getProductsPaged(): PagingSource<Int, ProductWithDetails>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product p INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' AND p.name LIKE ('%'|| :query ||'%') OR barcode LIKE ('%'|| :query ||'%') OR sku LIKE ('%'|| :query ||'%')")
    fun getProductsWithQueryPaged(query: String): PagingSource<Int, ProductWithDetails>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM product p INNER JOIN user ON storeId = currentStoreId WHERE localReferenceId = '1' AND p.name LIKE ('%'|| :query ||'%') OR barcode LIKE ('%'|| :query ||'%') OR sku LIKE ('%'|| :query ||'%') LIMIT 5")
    suspend fun getProductsWithQuery(query: String): List<Product>

    @Query("UPDATE product SET stock = CASE WHEN :type = 'IN' THEN stock + :amount ELSE stock - :amount END WHERE productId = :id")
    suspend fun updateProductAmountWithType(id: String, amount: Int, type: String)

    @Query("UPDATE product SET historicInStock = :inStock, historicOutStock = :outStock WHERE productId = :id")
    suspend fun updateProductHistoryStock(id: String, inStock: Int, outStock: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductSupplierCrossRef(refs: List<ProductSupplierCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductCategoriesCrossRef(refs: List<ProductCategoryCrossRef>)

    @Delete
    suspend fun deleteProductCategoriesCrossRef(refs: List<ProductCategoryCrossRef>)

    @Delete
    suspend fun deleteProductSupplierCrossRef(refs: List<ProductSupplierCrossRef>)

    @Query("SELECT * FROM productcategorycrossref WHERE productId = :productId")
    suspend fun getProductCategoriesCrossRefs(productId: String): List<ProductCategoryCrossRef>

    @Query("SELECT * FROM productsuppliercrossref WHERE productId = :productId")
    suspend fun getProductSupplierCrossRefs(productId: String): List<ProductSupplierCrossRef>

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
}
